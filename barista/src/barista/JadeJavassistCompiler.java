package barista;

import barista.ast.Node;
import barista.ast.Variable;
import barista.ast.script.ArgDeclList;
import barista.ast.script.FunctionDecl;
import barista.ast.script.Unit;
import barista.type.*;
import javassist.*;

import java.util.*;

/**
 * Jade compiler. Takes a reduced Jade AST, runs type analysis over it and then
 * emits it as Java source code, which is then compiled to Java classes.
 */
public class JadeJavassistCompiler implements JadeCompiler {
  private final Unit compilationUnit;
  private final String enclosingTypeName;

  private final Errors errors = new Errors();
  private final ClassPool pool = ClassPool.getDefault();
  private final List<EmittedFunction> functionsEmitted = new ArrayList<EmittedFunction>();

  private CtClass clazz;
  private String indent = "";
  private Map<String, Node> declarations = new HashMap<String, Node>();
  private StringBuilder declarationsEmitted = new StringBuilder();

  private StringBuilder out;

  private Scope currentScope;

  public JadeJavassistCompiler(String enclosingTypeName, Unit compilationUnit) {
    this.compilationUnit = compilationUnit;
    this.enclosingTypeName = enclosingTypeName;

    pool.importPackage("java.util");
    pool.importPackage("barista.runtime");
  }

  public Class<?> emit() {
    clazz = pool.makeClass(enclosingTypeName);
    currentScope = new ModuleScope(errors); // module-level scope comes with some predefined types.
    
    try {
      compileFunctions();

      if (errors.hasErrors()) {
        throw new RuntimeException(errors.toString());
      }
      return clazz.toClass();
    } catch (CannotCompileException e) {
      throw new RuntimeException(e);
    }
  }

  // emits text at the current indentation level.
  public void write(String st) {
    out.append(indent);
    out.append(st);
  }

  public void writePlain(String st) {
    out.append(st);
  }

  public void declareIfNecessary(Variable var) {
    assert currentScope != null; // Scope should never be null in an expr.

    // This variable has already been declared in this scope.
    if (null != currentScope.getVariable(var.name)) {
      return;
    }

    if (!(declarations.get(var.name) instanceof Variable)) {
      declarations.put(var.name, var);
      declarationsEmitted.append(var.egressType(currentScope));
      declarationsEmitted.append(" ");
      declarationsEmitted.append(var.name);
      declarationsEmitted.append(" = ");
      declarationsEmitted.append(var.value);
      declarationsEmitted.append(";\n");
    }
  }

  public Scope currentScope() {
    return currentScope;
  }

  public Errors errors() {
    return errors;
  }

  public void writePlain(int value) {
    out.append(value);
  }

  private void indent() {
    indent += "  ";
  }

  private void outdent() {
    if (indent.length() >= 2) {
      indent = indent.substring(0, indent.length() - 2);
    }
  }

  private void compileFunctions() throws CannotCompileException {
    // Maybe replace this with a templating system like StringTemplate or MVEL.

    for (FunctionDecl func : compilationUnit.functions()) {
      // Main should always be emitted as we know its type and there should
      // only ever be one concrete instance of it.
      if ("main".equals(func.name())) {
        // Infer main anyway, this helps trigger overload resolution for any called
        // polymorphic functions.
        compileConcreteFunction(func, Types.VOID, Arrays.<Type>asList());
        continue;
      }

      // Don't bother emitting functions that are polymorphic. They will get witnessed
      // and emitted from top-level concrete call paths.
      if (!func.isPolymorphic()) 
        compileConcreteFunction(func, func.inferType(currentScope), func.arguments().getTypes(currentScope));
      else
        compilePolymorphicFunction(func);
    }

    // Emit witnessed overloads of encountered polymorphic functions.
    for (Scope.Witness witness : currentScope.getWitnesses()) {
      compileConcreteFunction(witness.functionDecl, witness.returnType, witness.argumentTypes);
    }

    // Now Java-compile all the functions in one go for this module.
    System.out.println(functionsEmitted);

    // We need to first declare all the functions as abstract.
    for (EmittedFunction emittedFunction : functionsEmitted) {
      try {
        emittedFunction.method = CtNewMethod.make(emittedFunction.signature, clazz);
        clazz.addMethod(emittedFunction.method);
      } catch (CannotCompileException e) {
        errors.exception(e);
      }
    }

    // Then fill in function bodies for each abstract method.
    for (EmittedFunction emittedFunction : functionsEmitted) {
      emittedFunction.method.setBody(emittedFunction.body);
    }

    // Finally the class must be made concrete again (adding abstract methods
    // automatically turns the class abstract).
    clazz.setModifiers(clazz.getModifiers() & ~Modifier.ABSTRACT);
  }

  private void compileConcreteFunction(FunctionDecl func, Type returnType, List<Type> argTypes) {
    assert returnType != null;

    // Emit function signature first.
    out = new StringBuilder();
    write("public ");
    write(returnType.javaType());
    writePlain(" ");
    writePlain(func.name());
    writePlain("(");

    // Create a new lexical scope for every function.
    currentScope = new BasicScope(errors, currentScope);

    // emit function signature.
    compileFunctionSignature(func, argTypes);

    // Bake signature of function, then move on to the body.
    String signature = out.toString();
    out = new StringBuilder("{\n");

    indent();
    int declarationIndex = out.length();

    for (int i = 0; i < func.children().size(); i++) {
      Node node = func.children().get(i);
      // Analyze and determine the type of each statement.
      // TODO do some assertions after computing the egress type?
      Type egressType = node.egressType(currentScope);

      write("");
      int lineIndex = out.length();
      node.emit(this);
      write(";\n");

      // If this is the last line, return whatever was on the stack.
      if (i == func.children().size() - 1) {

        // HACK(dhanji): Workaround for void return types in Java. Simply
        // return. In the future we will want to use a sentinel that is
        // coercible into a Java type.
        if (Types.VOID.equals(egressType))
          write("\n  return;\n");
        else
          out.insert(lineIndex, "return ");
      }
    }

    // Emit any declarations before the body.
    out.insert(declarationIndex, declarationsEmitted);
    // Reset decls.
    declarationsEmitted = new StringBuilder();
    declarations = new HashMap<String, Node>();

    outdent();
    write("}\n");

    // pop function scope.
    currentScope = currentScope.parent();

    // Load this newly minted function into the containing scope.
    currentScope.load(func);
    functionsEmitted.add(new EmittedFunction(signature, out.toString()));
    
    out = null;
  }

  private void compilePolymorphicFunction(FunctionDecl func) {
    // Load this newly found function into the containing scope for (import it).
    currentScope.load(func);

    out = null;
  }

  private void compileFunctionSignature(FunctionDecl func, List<Type> argTypes) {
    List<Node> args = func.arguments().children();
    for (int i = 0; i < args.size(); i++) {
      ArgDeclList.Argument declaredArg = (ArgDeclList.Argument) args.get(i);
      writePlain(argTypes.get(i).javaType());
      writePlain(" ");
      writePlain(declaredArg.name());

      // Load the argument as a variable into the current scope, binding
      // it to the argument type. This may be a Type variable rather than
      // a concrete type.
      new Variable(declaredArg.name()).setEgressType(currentScope, argTypes.get(i), true);

      if (i < args.size() - 1)
        writePlain(", ");
    }
    writePlain(");\n");
  }

  private static class EmittedFunction {
    private final String signature;
    private final String body;

    private CtMethod method;

    private EmittedFunction(String signature, String body) {
      this.signature = signature;
      this.body = body;
    }

    @Override
    public String toString() {
      return signature + "\n" + body;
    }
  }
}
