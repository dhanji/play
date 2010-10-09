package barista.ast;

import barista.LoopCompiler;
import barista.type.Scope;
import barista.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract node in the parse tree.
 *
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public abstract class Node {
  // the rest of the tree under this node
  protected final List<Node> children = new ArrayList<Node>();

  public Node add(Node child) {
    children.add(child);
    return this;
  }

  public List<Node> children() {
    return children;
  }

  public abstract String toSymbol();

  public Type egressType(Scope scope) {
    throw new UnsupportedOperationException("Not implemented in " + getClass().getSimpleName());
  }

  public void emit(LoopCompiler loopCompiler) {
    throw new UnsupportedOperationException("Not implemented in " + getClass().getSimpleName());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        children + '}';
  }
}
