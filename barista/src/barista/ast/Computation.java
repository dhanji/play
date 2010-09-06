package barista.ast;

import barista.Emitter;

/**
 * Represents an expression fragment.
 */
public class Computation extends Node {

  @Override
  public void emit(Emitter emitter) {
    emitter.writePlain("(");
    for (Node child : children) {
      child.emit(emitter);
    }
    emitter.writePlain(")");
  }

  @Override
  public String toSymbol() {
    return "comput";
  }
}
