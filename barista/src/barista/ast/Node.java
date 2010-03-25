package barista.ast;

/**
 * A single AST node.
 */
public abstract class Node {
  public final String text;

  public Node(String text) {
    this.text = text;
  }
}
