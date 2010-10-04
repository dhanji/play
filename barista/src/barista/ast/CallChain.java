package barista.ast;

import barista.JadeCompiler;

/**
 * A call chain of dereferences or method calls, strung together.
 */
public class CallChain extends Node {

  @Override
  public void emit(JadeCompiler jadeCompiler) {
    for (int i = 0; i < children.size(); i++) {
      Node child = children.get(i);

      child.emit(jadeCompiler);

      // Only write a dot if there are more links to chain.
      if (i < children.size() - 1) {
        jadeCompiler.write(".");
      }
    }
  }

  @Override
  public String toSymbol() {
    return ".";
  }
}
