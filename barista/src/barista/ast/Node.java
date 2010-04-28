package barista.ast;

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

  public void add(Node child) {
    children.add(child);
  }

  public List<Node> children() {
    return children;
  }

  public abstract String toSymbol();

  public void insert(Node current) {
    children.add(0, current);
  }
}
