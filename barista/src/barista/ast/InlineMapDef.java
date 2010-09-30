package barista.ast;

import barista.Emitter;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

/**
 * Inline map definition. Entries in the map are declared
 * by alternating keys/values as 1st-level children of this node.
 */
public class InlineMapDef extends Node {
  private final boolean isTree;

  public InlineMapDef(boolean isTree) {
    this.isTree = isTree;
  }

  @Override
  public Type egressType(Scope scope) {
    return Types.MAP;
  }

  @Override
  public void emit(Emitter emitter) {
    emitter.writePlain(isTree ? "Trees" : "Maps");
    emitter.writePlain(".of(");

    // The map parameter type is determined from the most general key type
    // and the most general value type. For now though we'll just use the
    // type of the 'first' entry.
    Type keyType, valueType;
    if (children.isEmpty()) {
      keyType = valueType = Types.VOID;
    } else {
      // Keys/values alternate
      keyType = children.get(0).egressType(emitter.currentScope());
      valueType = children.get(1).egressType(emitter.currentScope());
    }

    emitter.writePlain("new Object[]{");

    for (int i = 0; i < children.size(); i += 2) {
      Node key = children.get(i);

      // Type check key.
      Type typeOfKey = key.egressType(emitter.currentScope());
      emitter.errors().check(keyType, typeOfKey, "map key");

      // Account for primitives that need to be boxed.
      boolean shouldBox = Types.isPrimitive(keyType);
      if (shouldBox) {
        emitter.writePlain("new ");
        emitter.writePlain(Types.boxedTypeOf(keyType));
        emitter.writePlain("(");
      }

      key.emit(emitter);

      if (shouldBox) {
        emitter.writePlain(")");
      }

      emitter.writePlain(", ");
      
      Node value = children.get(i + 1);
      // Type check value.
      Type typeOfValue = value.egressType(emitter.currentScope());
      emitter.errors().check(valueType, typeOfValue, "map value");

      // Account for primitives that need to be boxed.      
      shouldBox = Types.isPrimitive(valueType);
      if (shouldBox) {
        emitter.writePlain("new ");
        emitter.writePlain(Types.boxedTypeOf(valueType));
        emitter.writePlain("(");
      }

      value.emit(emitter);

      if (shouldBox) {
        emitter.writePlain(")");
      }

      if (i < children.size() - 2)
        emitter.writePlain(", ");
    }

    emitter.writePlain("})");
  }

  @Override
  public String toSymbol() {
    return isTree ? "tree" : "map";
  }
}
