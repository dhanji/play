package barista;

import barista.ast.Variable;
import barista.type.Errors;
import barista.type.Scope;
import barista.type.Type;

/**
 * Low level source emitting api.
 */
public interface Emitter {
  void write(String st);
  void writePlain(String st);
  void writePlain(int value);

  void declareIfNecessary(Variable var);
  Scope currentScope();

  Errors errors();
}
