package barista.ast;

import barista.JadeCompiler;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

/**
 * Inline list definition.
 */
public class InlineListDef extends Node {
  private final boolean isSet;

  public InlineListDef(boolean set) {
    isSet = set;
  }

  @Override
  public Type egressType(Scope scope) {
    return Types.LIST;
  }

  @Override
  public void emit(JadeCompiler jadeCompiler) {
    // TODO when performing type inference.
    // The list type is the most general type derivable from all
    // elements in the list.

    // For now we'll just use the type of the first element.
    Type bagType;
    if(children.isEmpty()) {
      bagType = Types.VOID;
    } else {
      bagType = children.get(0).egressType(jadeCompiler.currentScope());
    }

    jadeCompiler.writePlain(isSet ? "Sets" : "Lists");
    jadeCompiler.writePlain(".of(new ");
    jadeCompiler.writePlain(bagType.javaType());
    jadeCompiler.writePlain("[] {");

    for (int i = 0; i < children.size(); i++) {
      Node child = children.get(i);

      // Type check children.
      jadeCompiler.errors().check(bagType, child.egressType(jadeCompiler.currentScope()),
          isSet ? "set element" : "list element");

      child.emit(jadeCompiler);

      if (i < children.size() - 1)
        jadeCompiler.writePlain(", ");
    }
    jadeCompiler.writePlain("})");
  }

  @Override
  public String toSymbol() {
    return isSet ? "set" : "list";
  }
}
