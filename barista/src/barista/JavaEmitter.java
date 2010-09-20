package barista;

import barista.ast.Node;
import barista.ast.Variable;
import barista.ast.script.FunctionDecl;
import barista.ast.script.Unit;
import barista.type.Scope;
import barista.type.Type;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Java code emitter, takes a reduced AST and emits Java code.
 */
public class JavaEmitter implements Emitter {
  private final Unit compilationUnit;
  private final String enclosingTypeName;
  private final Set<CompileError> errors = new LinkedHashSet<CompileError>();

  private final ClassPool pool = ClassPool.getDefault();

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
    // Maybe replace this with a templating system like StringTemplate or MVEL.

    clazz = pool.makeClass(enclosingTypeName);
    try {
      emitFunctions();

      if (!errors.isEmpty()) {
        throw new RuntimeException(CompileError.toString(errors));
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

  public void addError(CompileError err) {
    errors.add(err);
  }

  public Scope currentScope() {
    return currentScope;
  }

  public void check(Type expected, Type actual, String message) {
    if (!expected.equals(actual)) {
      errors.add(new CompileError("Type mismatch, expected " + expected.name() + " but was "
          + actual.name() + " in " + message + "."));
    }
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
    for (FunctionDecl func: compilationUnit.functions()) {
      out = new StringBuilder();
      write("public void ");
      writePlain(func.name());
      writePlain("() {\n");

      // Create a new lexical scope for every function.
      currentScope = new Scope(errors);

      indent();
      int declarationIndex = out.length();

      for (int i = 0; i < func.children().size(); i++) {
        Node node = func.children().get(i);
        // Analyze and determine the type of each computation chain.
        // TODO do some assertions after computing the egress type?
        node.egressType(currentScope);

        write("");
        int lineIndex = out.length();
        node.emit(this);
        write(";\n");

        // If this is the last line, return whatever was on the stack.
        if (i == func.children().size() - 1) {
          // TODO work out void return types.
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

      System.out.println(out.toString());

      try {
        clazz.addMethod(CtNewMethod.make(out.toString(), clazz));
      } catch (CannotCompileException e) {
        addError(new CompileError(e.getMessage()));
      }
      out = null;
    }
  }
}
