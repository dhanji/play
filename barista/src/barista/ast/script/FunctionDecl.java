package barista.ast.script;

import barista.Parser;
import barista.ast.Node;

/**
 * A declaration of a function. May be free or a member of a class.
 */
public class FunctionDecl extends Node {
  private final String name;
  private final Node arguments;

  public FunctionDecl(String name, Node arguments) {
    this.name = name;
    this.arguments = arguments == null ? new ArgDeclList() : arguments;
  }

  public String name() {
    return name;
  }

  @Override
  public String toSymbol() {
    return name + ": " + Parser.stringify(arguments) + " ->";
  }
}
