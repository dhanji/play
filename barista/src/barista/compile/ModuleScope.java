package barista.compile;

import barista.ast.script.ArgDeclList;
import barista.ast.script.FunctionDecl;
import barista.compile.BasicScope;
import barista.type.Errors;
import barista.type.Types;

/**
 * A lexical scope meant specifically for use at the module level (akin to
 * packages in Java).
 */
public class ModuleScope extends BasicScope {
  public ModuleScope(Errors errors) {
    super(errors, null);

    init();
  }

  private void init() {
    // These types are visible in every module.
    load(Types.INTEGER);
    load(Types.STRING);
    load(Types.VOID);

    load(Types.LIST);
    load(Types.MAP);

    ArgDeclList args = new ArgDeclList();
    args.add(new ArgDeclList.Argument("arg1", "Integer"));
    load(new FunctionDecl("print", args));
  }
}
