package barista.ast;

import barista.LoopCompiler;
import barista.type.Scope;
import barista.type.Type;

/**
 * A call chain of dereferences or method calls, strung together.
 */
public class CallChain extends Node {

  @Override
  public Type egressType(Scope scope) {
    assert !children.isEmpty() : "Empty call chain. Parsing bug?";
    // The egress type of the call chain is the return type of the
    // last method in the chain.
    return children.get(children.size() - 1).egressType(scope);
  }

  @Override
  public void emit(LoopCompiler loopCompiler) {
    for (int i = 0; i < children.size(); i++) {
      Node child = children.get(i);

      child.emit(loopCompiler);

      // Only write a dot if there are more links to chain.
      if (i < children.size() - 1) {
        loopCompiler.write(".");
      }
    }
  }

  @Override
  public String toSymbol() {
    return ".";
  }
}
