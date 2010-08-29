package barista.ast;

/**
 * Inline list definition.
 */
public class InlineListDef extends Node {
  @Override
  public String toSymbol() {
    return "list";
  }
}
