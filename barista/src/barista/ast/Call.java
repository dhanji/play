package barista.ast;

import barista.Parser;
import barista.Token;

import java.util.List;

/**
 * Represents a method call or member dereference.
 */
public class Call extends Node {
  private final String name;
  private final boolean isMethod;
  private final Arglist args;

  public Call(String name, boolean method, Arglist args) {
    this.name = name;
    this.isMethod = method;
    this.args = args;
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
