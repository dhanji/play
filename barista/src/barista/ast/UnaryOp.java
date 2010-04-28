package barista.ast;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class UnaryOp extends Node {

  @Override
  public String toString() {
    return children.toString();
  }

  @Override
  public String toSymbol() {
    return ""; // unary ops do nothing by themselves
  }
}
