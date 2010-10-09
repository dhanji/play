package barista;

import barista.ast.Variable;
import barista.type.Errors;
import barista.type.Scope;
import barista.type.Type;

/**
 * Low level binary emitting api.
 */
public interface LoopCompiler {
  void write(String st);
  void write(int value);

  void declareIfNecessary(Variable var);
  Scope currentScope();

  Errors errors();
}
