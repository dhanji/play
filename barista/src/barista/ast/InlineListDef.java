package barista.ast;

import barista.Emitter;
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
  public void emit(Emitter emitter) {
    // TODO when performing type inference.
    // The list type is the most general type derivable from all
    // elements in the list.

    // For now we'll just use the type of the first element.
    Type bagType;
    if(children.isEmpty()) {
      bagType = Types.VOID;
    } else {
      bagType = children.get(0).egressType(emitter.currentScope());
    }

    emitter.writePlain(isSet ? "Sets" : "Lists");
    emitter.writePlain(".of(new ");
    emitter.writePlain(bagType.javaType());
    emitter.writePlain("[] {");

    for (int i = 0; i < children.size(); i++) {
      Node child = children.get(i);

      // Type check children.
      emitter.check(bagType, child.egressType(emitter.currentScope()),
          isSet ? "set element" : "list element");

      child.emit(emitter);

      if (i < children.size() - 1)
        emitter.writePlain(", ");
    }
    emitter.writePlain("})");
  }

  @Override
  public String toSymbol() {
    return isSet ? "set" : "list";
  }
}
