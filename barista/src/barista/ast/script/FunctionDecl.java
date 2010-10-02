package barista.ast.script;

import barista.Parser;
import barista.ast.Arglist;
import barista.ast.Node;
import barista.ast.Variable;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * A declaration of a function. May be free or a member of a class.
 */
public class FunctionDecl extends Node {
  private final String name;
  private final ArgDeclList arguments;

  public FunctionDecl(String name, ArgDeclList arguments) {
    this.name = name;
    this.arguments = arguments == null ? new ArgDeclList() : arguments;
  }

  public String name() {
    return name;
  }

  /**
   * Simple type inference algorithm, uses egress type of expression block to
   * determine the function type, given type-bound arguments.
   */
  public Type inferType(Scope scope, Arglist args) {

    // Step 1: Bind all given arguments are in current scope as local vars.

    List<Type> bound = new ArrayList<Type>(args.children().size());
    for (int i = 0; i < arguments.children().size(); i++) {
      ArgDeclList.Argument argDecl = (ArgDeclList.Argument) arguments.children().get(i);

      Variable argument = new Variable(argDecl.name());
      // Determine the egress type.
      Type type = args.children().get(i).egressType(scope);

      // Set it as a local variable of the inferred type.
      argument.setEgressType(scope, type, true);

      // Add it to the function signature.
      bound.add(type);
    }

    return inferType(scope, bound);
  }

  /**
   * Same as {@link #inferType(barista.type.Scope, barista.ast.Arglist)} except that
   * it attempts to work out the argument types universally for this function. In other
   * words, it assumes that this is not a polymorphic function.
   */
  public Type inferType(Scope scope) {
    // arguments().getTypes() should never be null if the function is concrete.
    return inferType(scope, arguments().getTypes(scope));
  }

  private Type inferType(Scope scope, List<Type> bound) {
    // Must go through all the child nodes and assign types to everything from
    // the argument list. Something like the Hindley-Milner algorithm.


    // Step 2: Traverse each statement and determine its egress type.
    // This binds any further unbound symbols with inferred types.
    Type inferred = null;
    for (Node statement : children) {
      inferred = statement.egressType(scope);
    }

    // Step 3: Solve the return type of this function by taking the last statement's
    // egress type. Otherwise the function had an empty body.
    if (null == inferred)
      inferred = Types.VOID;

    // Step 4: Witness this solution so that the specific signature can be emitted
    // as a Java overload.
    scope.witness(this, bound, inferred);

    return inferred;
  }

  public ArgDeclList arguments() {
    return arguments;
  }

  public boolean isPolymorphic() {
    for (Node node : arguments.children()) {
      ArgDeclList.Argument argument = (ArgDeclList.Argument) node;

      // If any of the arguments are polymorphic, then the entire function
      // is polymorphic.
      if (argument.type() == null)
        return true;
    }
    return false;
  }

  @Override
  public String toSymbol() {
    return name + ": " + Parser.stringify(arguments) + " ->";
  }
}
