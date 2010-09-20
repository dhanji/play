package barista.type;

import barista.CompileError;
import barista.ast.Variable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A symbol scope, of a lexical scoping model.
 */
public class Scope {
  private final Map<String, Variable> variables = new HashMap<String, Variable>();
  private final Collection<CompileError> errors;

  public Scope(Collection<CompileError> errors) {
    this.errors = errors;
  }

  public void load(Variable v) {
    variables.put(v.name, v);
  }

  public Variable get(String name) {
    return variables.get(name);
  }

  // TODO improve the error reporting story.
  public void unknownSymbol(String var) {
    // TODO maybe a fuzzy match and suggest?
    errors.add(new CompileError("Use of unknown symbol " + var));
  }

  public void check(Type expected, Type actual, String message) {
    if (!expected.equals(actual)) {
      errors.add(new CompileError("Type mismatch, expected " + expected.name() + " but was "
          + actual.name() + " in " + message + "."));
    }
  }
}
