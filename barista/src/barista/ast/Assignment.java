package barista.ast;

import barista.Parser;

/**
 * A right to left assignment statement.
 */
public class Assignment extends Node {
  private final Node condition;
  public Assignment(Node condition) {
    this.condition = condition;
  }

  public Assignment() {
    this(null);
  }

  @Override
  public String toSymbol() {
    if (condition == null) {
      return "=";
    }
    return "=if" + Parser.stringify(condition);
  }
}
