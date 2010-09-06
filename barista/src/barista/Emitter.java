package barista;

import barista.ast.Variable;

/**
 * Low level source emitting api.
 */
public interface Emitter {
  void write(String st);
  void writePlain(String st);
  void writePlain(int value);

  void declareIfNecessary(Variable var);
}
