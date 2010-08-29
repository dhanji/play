package barista.ast;

import barista.Parser;

/**
 * A list comprehension
 */
public class Comprehension extends Node {
  private final Node var;
  private final Node inList;
  private final Node filter;

  public Comprehension(Node var, Node inList, Node filter) {
    this.var = var;
    this.inList = inList;
    this.filter = filter;
  }

  @Override
  public String toSymbol() {
    return "for " + var.toSymbol() + " in " + Parser.stringify(inList)
        + (filter == null ? "" : " if " + Parser.stringify(filter));
  }
}
