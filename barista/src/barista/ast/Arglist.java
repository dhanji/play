package barista.ast;

import barista.Emitter;
import barista.Parser;

/**
 * An argument list to a function. May be positional or named.
 *
 * example:
 * (x, y, z)
 *
 * or:
 *
 * (arg0: x, arg1: x + 1, arg2: [1..3])
 */
public class Arglist extends Node {
  private final boolean positional;

  public Arglist(boolean positional) {
    this.positional = positional;
  }

  public boolean isPositional() {
    return positional;
  }

  public static class NamedArg extends Node {
    private final String name;
    private Node arg;

    public NamedArg(String name, Node arg) {
      this.name = name;
      this.arg = arg;
    }

    @Override
    public String toSymbol() {
      return name + ": " + Parser.stringify(arg);
    }
  }

  @Override
  public void emit(Emitter emitter) {
    emitter.writePlain("(");

    for (int i = 0; i < children.size(); i++) {
      Node child = children.get(i);
      child.emit(emitter);

      if (i < children.size() - 1)
        emitter.writePlain(", ");
    }
    emitter.writePlain(")");
  }

  @Override
  public String toSymbol() {
    return children().isEmpty() ? "()" : "()=";
  }
}
