package barista.ast.script;

import barista.Parser;
import barista.ast.Arglist;
import barista.ast.Node;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

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

  public Type inferType(Scope scope, Arglist args) {
    // TODO temp
    return Types.VOID;
  }

  public ArgDeclList arguments() {
    return arguments;
  }

  @Override
  public String toSymbol() {
    return name + ": " + Parser.stringify(arguments) + " ->";
  }
}
