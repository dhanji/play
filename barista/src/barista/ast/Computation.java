package barista.ast;

import barista.Emitter;
import barista.type.Scope;
import barista.type.Type;

/**
 * Represents an expression fragment.
 */
public class Computation extends Node {

  @Override
  public Type egressType(Scope scope) {
    // All children should have a common type which will egress this
    // computation. Alternatively, they should egress a structural type.
    Type commonType = null;
    for (Node child : children) {
      if (commonType == null) {
        commonType = child.egressType(scope);
      } else {
        scope.check(commonType, child.egressType(scope), "expression");
      }
    }

    return commonType;
  }

  @Override
  public void emit(Emitter emitter) {
    emitter.writePlain("(");
    for (Node child : children) {
      child.emit(emitter);
    }
    emitter.writePlain(")");
  }

  @Override
  public String toSymbol() {
    return "comput";
  }
}
