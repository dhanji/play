package barista.type;

import barista.ast.Variable;
import barista.ast.script.FunctionDecl;

import java.util.*;

/**
 * A symbol scope, of a lexical scoping model.
 */
public class BasicScope implements Scope {
  private final Map<String, Variable> variables = new HashMap<String, Variable>();
  private final Map<String, FunctionDecl> functions = new HashMap<String, FunctionDecl>();
  private final Map<String, Type> types = new HashMap<String, Type>();

  private final Map<String, String> variablesToArgumentNames = new HashMap<String, String>();

  private final List<Witness> witnesses = new ArrayList<Witness>();

  private final Errors errors;

  private Scope parent;

  public BasicScope(Errors errors, Scope parent) {
    this.errors = errors;
    this.parent = parent;
  }

  public void load(Variable v, boolean isArgument) {
    variables.put(v.name, v);
    if (isArgument) {
      variablesToArgumentNames.put(v.name, "$" + (variablesToArgumentNames.size() + 1));
    }
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

  public String resolveVariableName(String name) {
    String altName = variablesToArgumentNames.get(name);

    return null == altName ? name : altName;
  }

  public Scope parent() {
    return parent;
  }

  public Errors errors() {
    return errors;
  }

  /**
   * Witnesses the return type of a polymorphic function bound over a given set
   * of quantified argument types.
   *
   * @param functionDecl the polymorphic function declaration.
   * @param bound the argument list of concrete type arguments.
   * @param inferred the witness of {@code functionDecl}. I.e. it's
   *     concrete inferred type.
   */
  public void witness(FunctionDecl functionDecl, List<Type> bound, Type inferred) {
    // HACK(dhanji): Workaround for not knowing the owning scope of a function.
    // Witness it in the parent-most scope.
    if (parent == null)
      witnesses.add(new Witness(functionDecl, bound, inferred));
    else
      parent.witness(functionDecl, bound,inferred);
  }

  public List<Witness> getWitnesses() {
    return witnesses;
  }
}
