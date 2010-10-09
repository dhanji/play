package barista;

import barista.ast.Variable;
import barista.type.Errors;
import barista.type.Scope;

/**
 * Low level binary emitting api.
 */
public interface LoopCompiler {
  void write(String st);
  void write(int value);
  void writeAtMarker(String st);

  void mark();

  void declareIfNecessary(Variable var);
  Scope currentScope();

  Errors errors();
}
