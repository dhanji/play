package barista.ast;

import barista.Emitter;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class Variable extends Node {
  public final String name;

  // TODO determine type during compile
  public String type = "int";

  public Variable(String name) {
    this.name = name;
  }

  @Override
  public void emit(Emitter emitter) {
    // Declare if necessary.
    emitter.declareIfNecessary(this);

    emitter.writePlain(name);
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
