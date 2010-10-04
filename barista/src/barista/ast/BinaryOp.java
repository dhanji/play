package barista.ast;

import barista.JadeCompiler;
import barista.Token;
import barista.type.Scope;
import barista.type.Type;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class BinaryOp extends Node {
  public final Token operator;

  public BinaryOp(Token operator) {
    this.operator = operator;
  }

  @Override
  public Type egressType(Scope scope) {
    // Binary ops only have one child.
    return children.get(0).egressType(scope);
  }

  @Override
  public void emit(JadeCompiler jadeCompiler) {
    jadeCompiler.write(" ");
    jadeCompiler.write(operator.value);
    jadeCompiler.write(" ");
    
    children().get(0).emit(jadeCompiler);
  }

  @Override
  public String toString() {
    return "BinaryOp{" +
        "operator=" + operator +
        ":: " + children +
        '}';
  }

  @Override
  public String toSymbol() {
    return operator.value;
  }
}
