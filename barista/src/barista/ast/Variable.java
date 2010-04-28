package barista.ast;

import barista.Token;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class Variable extends Node {
  private final String name;

  public Variable(String name) {
    this.name = name;
  }

  @Override
  public String toSymbol() {
    return name;
  }

  @Override
  public String toString() {
    return "Variable{" +
        "name='" + name + '\'' +
        '}';
  }
}
