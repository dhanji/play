package barista.ast;

import barista.Emitter;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class Variable extends Node {
  public final String name;
  private Type type = Types.INTEGER; // defaults to int
  public String value = Types.INTEGER.defaultValue();

  public Variable(String name) {
    this.name = name;
  }

  public void setEgressType(Scope scope, Type type) {
    scope.load(this);

    this.type = type;
    this.value = type.defaultValue();
  }

  @Override
  public Type egressType(Scope scope) {
    Variable variable = scope.getVariable(name);

    // If this variable is already declared in this scope,
    // then use its type.
    if (variable != null) {
      this.type = variable.type;
    } else {
      scope.errors().unknownSymbol(name);
    }
    return type;
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
