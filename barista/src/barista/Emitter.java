package barista;

import barista.ast.Variable;
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

  void addError(CompileError err);

  Scope currentScope();

  void check(Type expected, Type actual, String message);
}
