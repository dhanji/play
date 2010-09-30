package barista;

import barista.ast.Node;
import barista.ast.Variable;
import barista.ast.script.ArgDeclList;
import barista.ast.script.FunctionDecl;
import barista.ast.script.Unit;
import barista.type.*;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

import java.util.*;

/**
 * Java code emitter, takes a reduced AST and emits Java code.
 */
public class JavaEmitter implements Emitter {
  private final Unit compilationUnit;
  private final String enclosingTypeName;

  private final Errors errors = new Errors();
  private final ClassPool pool = ClassPool.getDefault();
  private final List<String> functionsEmitted = new ArrayList<String>();

  private CtClass clazz;
  private String indent = "";
  private Map<String, Node> declarations = new HashMap<String, Node>();
  private StringBuilder declarationsEmitted = new StringBuilder();

  private StringBuilder out;

  private Scope currentScope;

  public JavaEmitter(String enclosingTypeName, Unit compilationUnit) {
    this.compilationUnit = compilationUnit;
    this.enclosingTypeName = enclosingTypeName;

    pool.importPackage("java.util");
    pool.importPackage("barista.runtime");
  }

  public Class<?> emit() {
    clazz = pool.makeClass(enclosingTypeName);
    currentScope = new ModuleScope(errors); // module-level scope comes with some predefined types.
    
    try {
      emitFunctions();

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

  private void emitFunctions() throws CannotCompileException {
    // Maybe replace this with a templating system like StringTemplate or MVEL.

    FunctionDecl main = null;
    for (FunctionDecl func : compilationUnit.functions()) {
      // Main should always be emitted as we know its type and there should
      // only ever be one concrete instance of it.
      if ("main".equals(func.name()))
        emitSingleFunction(func, Types.VOID, Arrays.<Type>asList());
      else
        emitSingleFunction(func, null, null);
    }

    // Emit witnessed overloads of encountered polymorphic functions.
    for (Scope.Witness witness : currentScope.getWitnesses()) {
      emitSingleFunction(witness.functionDecl, witness.returnType, witness.argumentTypes);
    }

    // Now compile all the functions in one go for this module.
    Collections.reverse(functionsEmitted);
    System.out.println(functionsEmitted);
    for (String emittedFunc : functionsEmitted) {
//      try {
        clazz.addMethod(CtNewMethod.make(emittedFunc, clazz));
//      } catch (CannotCompileException e) {
//        errors.exception(e);
//      }
    }
  }

  private void emitSingleFunction(FunctionDecl func, Type returnType, List<Type> argTypes) {
    // Suppress the emission of this function if it is not a witnessed concrete overload.
    boolean suppress = returnType == null;
    if (suppress) {
      returnType = Types.VOID;
    }

    out = new StringBuilder();
    write("public ");
    write(returnType.javaType());
    writePlain(" ");
    writePlain(func.name());
    writePlain("(");

    // Create a new lexical scope for every function.
    currentScope = new BasicScope(errors, currentScope);
    emitFunctionSignature(func);

    indent();
    int declarationIndex = out.length();

    for (int i = 0; i < func.children().size(); i++) {
      Node node = func.children().get(i);
      // Analyze and determine the type of each computation chain.
      // TODO do some assertions after computing the egress type?
      Type egressType = node.egressType(currentScope);

      write("");
      int lineIndex = out.length();
      node.emit(this);
      write(";\n");

      // If this is the last line, return whatever was on the stack.
      if (i == func.children().size() - 1) {

        // HACK(dhanji): Workaround for void return types in Java. Simply
        // return null. In the future we will want to use a sentinel that
        // is coercible into a type. I.e. no nulls.
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
    
    if (!suppress) {
      functionsEmitted.add(out.toString());
    }
    out = null;
  }

  private void emitFunctionSignature(FunctionDecl func) {
    List<Node> args = func.arguments().children();
    for (int i = 0; i < args.size(); i++) {
      ArgDeclList.Argument declaredArg = (ArgDeclList.Argument) args.get(i);
      writePlain(declaredArg.egressType(currentScope).javaType());
      writePlain(" ");
      writePlain(declaredArg.name());

      // Load the argument as a variable into the current scope, binding
      // it to the argument type. This may be a Type variable rather than
      // a concrete type.
      new Variable(declaredArg.name()).setEgressType(currentScope,
          declaredArg.egressType(currentScope));

      if (i < args.size() - 1)
        writePlain(", ");
    }
    writePlain(") {\n");
  }
}
