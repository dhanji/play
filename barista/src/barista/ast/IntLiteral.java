package barista.ast;

import barista.Emitter;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class IntLiteral extends Node {
  public final int value;
  public IntLiteral(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public void emit(Emitter emitter) {
    emitter.writePlain(value);
  }

  @Override
  public String toString() {
    return "IntLiteral{" +
        "value=" + value +
        '}';
  }

  @Override
  public String toSymbol() {
    return "" + value;
  }
}
