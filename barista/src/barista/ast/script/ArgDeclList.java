package barista.ast.script;

import barista.ast.Node;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

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

    public String name() {
      return name;
    }

    public String type() {
      return type;
    }

    @Override
    public Type egressType(Scope scope) {
      return Types.VOID;
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
