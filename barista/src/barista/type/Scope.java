package barista.type;

import barista.CompileError;
import barista.ast.Variable;
import barista.ast.script.FunctionDecl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A symbol scope, of a lexical scoping model.
 */
public class Scope {
  private final Map<String, Variable> variables = new HashMap<String, Variable>();
  private final Map<String, FunctionDecl> functions = new HashMap<String, FunctionDecl>();
  private final Map<String, Type> types = new HashMap<String, Type>();

  private final Collection<CompileError> errors;
  private Scope parent;

  public Scope(Collection<CompileError> errors, Scope parent) {
    this.errors = errors;
    this.parent = parent;
  }

  public Scope(Collection<CompileError> errors) {
    this.errors = errors;
  }

  public void load(Variable v) {
    variables.put(v.name, v);
  }

  public void load(FunctionDecl func) {
    functions.put(func.name(), func);
  }

  public void load(Type type) {
    types.put(type.name(), type);
  }

  public Variable getVariable(String name) {
    Variable variable = variables.get(name);

    // Keep looking up the scope chain.
    if (null == variable && null != parent) {
      variable = parent.getVariable(name);
    }
    return variable;
  }

  public FunctionDecl getFunction(String name) {
    FunctionDecl function = functions.get(name);

    // Keep looking up the scope chain.
    if (null == function && null != parent) {
      function = parent.getFunction(name);
    }

    return function;
  }

  public Type getType(String name) {
    Type type = types.get(name);

    // Keep looking up the scope chain.
    if (null == type && null != parent) {
      type = parent.getType(name);
    }
    return type;
  }

  public Scope parent() {
    return parent;
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
