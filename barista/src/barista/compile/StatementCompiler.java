package barista.compile;

import barista.ast.*;

import java.util.List;

/**
 * Compiles a single statement Node within a function body, emitting the result
 * as Java source code.
 */
class StatementCompiler {
  private final Scope scope;
  private StringBuilder out;

  public StatementCompiler(Scope scope) {
    this.scope = scope;
  }

  public String compile(Node statement) {
    out = new StringBuilder();

    if (statement instanceof Call) {
      compile((Call) statement);
    } else if (statement instanceof BinaryOp) {
      compile((BinaryOp) statement);
    } else if (statement instanceof Variable) {
      compile((Variable) statement);
    }

    return out.toString();
  }

  public void compile(Call call) {
    out.append(call.name);
    if (call.isFunction) {
      compile(call.args());
    }
  }

  public void compile(CallArguments callArguments) {
    out.append("(");

    List<Node> children = callArguments.children();
    for (int i = 0; i < children.size(); i++) {
      compile(children.get(i));

      if (i < children.size() - 1)
        out.append(", ");
    }
    out.append(")");
  }

  public void compile(BinaryOp op) {
    out.append(" ");
    out.append(op.operator.value);
    out.append(" ");

    compile(op.onlyChild());
  }

  public void compile(Variable var) {
    // Declare if necessary.
    scope.maybeDeclare(var);

    out.append(scope.getLocalVariable(var.name).getName());
  }
}
