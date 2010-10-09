package barista.ast;

import barista.LoopCompiler;
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
        scope.errors().check(commonType, child.egressType(scope), "expression");
      }
    }

    return commonType;
  }

  @Override
  public void emit(LoopCompiler loopCompiler) {
    loopCompiler.write("(");
    for (Node child : children) {
      child.emit(loopCompiler);
    }
    loopCompiler.write(")");
  }

  @Override
  public String toSymbol() {
    return "comput";
  }
}
