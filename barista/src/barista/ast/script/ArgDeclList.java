package barista.ast.script;

import barista.ast.Node;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a declaration of arguments of a function.
 */
public class ArgDeclList extends Node {

  // Memo field
  private List<Type> types;

  /**
   * Returns a list of argument types of this argument list. If types are
   * unknown for any of the arguments, this method will return null.
   */
  public List<Type> getTypes(Scope scope) {
    // TODO(dhanji): You pay the cost of type lookup every time if this function is
    // polymorphic.
    if (types != null) {
      return types;
    }

    types = new ArrayList<Type>(children.size());
    for (Node child : children) {
      Argument argument = (Argument) child;

      if (argument.type() == null) {
        return null;
      }

      types.add(scope.getType(argument.type()));
    }

    return types;
  }

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
      return scope.getType(type);
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
