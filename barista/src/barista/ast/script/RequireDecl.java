package barista.ast.script;

import barista.ast.Node;

import java.util.List;

/**
 * Import declaration at the top of a script.
 */
public class RequireDecl extends Node {
  private final List<String> moduleChain;

  public RequireDecl(List<String> moduleChain) {
    this.moduleChain = moduleChain;
  }

  @Override
  public String toSymbol() {
    return "require " + moduleChain;
  }
}
