package barista.ast;

import barista.Emitter;
import barista.Parser;

/**
 * Represents a method call or member dereference.
 */
public class Call extends Node {
  private final String name;
  private final boolean isMethod;
  private Arglist args;

  public Call(String name, boolean method, Arglist args) {
    this.name = name;
    this.isMethod = method;
    this.args = args;
  }

  public Arglist args() {
    return args;
  }

  @Override
  public void emit(Emitter emitter) {
    emitter.writePlain(name);
    if (isMethod) {
      args.emit(emitter);
    }
  }

  @Override
  public String toString() {
    return "Call{" + name + (isMethod ? args.toString() : "") + "}";
  }

  @Override
  public String toSymbol() {
    return name + (isMethod ? Parser.stringify(args) : "");
  }
}
