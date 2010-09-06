package barista;

import barista.ast.Node;
import barista.ast.Variable;
import barista.ast.script.FunctionDecl;
import barista.ast.script.Unit;

import java.util.HashMap;
import java.util.Map;

/**
 * Java code emitter, takes a reduced AST and emits Java code.
 */
public class JavaEmitter implements Emitter {
  private final Unit compilationUnit;
  private final String enclosingTypeName;

  private final StringBuilder out = new StringBuilder();

  private String indent = "";
  private Map<String, Node> declarations = new HashMap<String, Node>();
  private StringBuilder declarationsEmitted = new StringBuilder();

  public JavaEmitter(String enclosingTypeName, Unit compilationUnit) {
    this.compilationUnit = compilationUnit;
    this.enclosingTypeName = enclosingTypeName;
  }

  public String emit() {
    // Maybe replace this with a templating system like StringTemplate or MVEL.

    write("public class ");
    write(enclosingTypeName);
    write(" {\n");

    indent();
    emitFunctions();
    outdent();

    write("\n}");

    return out.toString();
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
    if (!(declarations.get(var.name) instanceof Variable)) {
      declarations.put(var.name, var);
      declarationsEmitted.append(var.type);
      declarationsEmitted.append(" ");
      declarationsEmitted.append(var.name);
      declarationsEmitted.append(";\n");
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

  private void emitFunctions() {
    for (FunctionDecl func: compilationUnit.functions()) {
      write("public Object ");
      writePlain(func.name());
      writePlain("() {\n");

      indent();
      int declarationIndex = out.length();
      for (Node node : func.children()) {
        write("");
        node.emit(this);
        write(";\n");
      }

      // Emit any declarations before the body.
      out.insert(declarationIndex, declarationsEmitted);
      // Reset decls.
      declarationsEmitted = new StringBuilder();
      declarations = new HashMap<String, Node>();

      outdent();

      write("}\n");
    }
  }
}
