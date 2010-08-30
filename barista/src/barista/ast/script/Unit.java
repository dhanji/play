package barista.ast.script;

import barista.ast.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A compilation unit containing imports classes, functions, etc. Represents a single file.
 */
public class Unit {
  private final ModuleDecl module;

  private final Set<RequireDecl> imports = new HashSet<RequireDecl>();
  private final Map<String, FunctionDecl> functions = new HashMap<String, FunctionDecl>();
//  private final Map<String, FunctionDecl> classes = new HashMap<String, FunctionDecl>();

  public Unit(ModuleDecl module) {
    this.module = module;
  }

  public FunctionDecl get(String name) {
    return functions.get(name);
  }

  public void add(FunctionDecl node) {
    functions.put(node.name(), node);
  }

  public void add(RequireDecl node) {
    imports.add(node);
  }
}
