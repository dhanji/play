package barista.ast;

import barista.Emitter;

/**
 * A call chain of dereferences or method calls, strung together.
 */
public class CallChain extends Node {

  @Override
  public void emit(Emitter emitter) {
    for (int i = 0; i < children.size(); i++) {
      Node child = children.get(i);

      child.emit(emitter);

      // Only write a dot if there are more links to chain.
      if (i < children.size() - 1) {
        emitter.writePlain(".");
      }
    }
  }

  @Override
  public String toSymbol() {
    return ".";
  }
}
