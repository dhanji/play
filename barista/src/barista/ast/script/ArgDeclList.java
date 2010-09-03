package barista.ast.script;

import barista.ast.Node;

/**
 *
 */
public class ArgDeclList extends Node {
  public static class Argument extends Node {
    private final String name;
    private final String type;
    
    public Argument(String name, String type) {
      this.name = name;
      this.type = type;
    }

    @Override
    public String toSymbol() {
      return name + (type == null ? "" : ":" + type);
    }
  }

  @Override
  public String toSymbol() {
    return children().isEmpty() ? "()" : "()=";
  }
}
