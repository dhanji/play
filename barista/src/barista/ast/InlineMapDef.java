package barista.ast;

/**
 * Inline map definition. Entries in the map are declared
 * by alternating keys/values as 1st-level children of this node.
 */
public class InlineMapDef extends Node {
  @Override
  public String toSymbol() {
    return "map";
  }
}
