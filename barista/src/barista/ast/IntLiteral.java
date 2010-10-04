package barista.ast;

import barista.JadeCompiler;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class IntLiteral extends Node {
  public final int value;
  public IntLiteral(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public Type egressType(Scope scope) {
    return Types.INTEGER;
  }

  @Override
  public void emit(JadeCompiler jadeCompiler) {
    jadeCompiler.write(value);
  }

  @Override
  public String toString() {
    return "IntLiteral{" +
        "value=" + value +
        '}';
  }

  @Override
  public String toSymbol() {
    return "" + value;
  }
}
