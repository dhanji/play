package barista.ast;

import barista.JadeCompiler;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class StringLiteral extends Node {
  private final String name;

  public StringLiteral(String name) {
    // Strip quotes...
    this.name = name;
  }

  @Override
  public Type egressType(Scope scope) {
    return Types.STRING;
  }

  @Override
  public void emit(JadeCompiler jadeCompiler) {
    jadeCompiler.write("\"");
    jadeCompiler.write(name.substring(1, name.length() - 1));
    jadeCompiler.write("\"");
  }

  @Override
  public String toSymbol() {
    return name;
  }

  @Override
  public String toString() {
    return "String{" +
        "'" + name + '\'' +
        '}';
  }
}