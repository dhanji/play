package barista.ast;

import barista.Emitter;
import barista.Parser;
import barista.type.Scope;
import barista.type.Type;
import barista.type.Types;

/**
 * Represents a method call or member dereference.
 */
public class Call extends Node {
  private final String name;
  private final boolean isMethod;
  private Arglist args;

  public Call(String name, boolean method, Arglist args) {
    // HACK! rewrites print calls to Java sout
    this.name = "print".equals(name) ? "System.out.println" : name;
    this.isMethod = method;
    this.args = args;
  }

  public Arglist args() {
    return args;
  }

  @Override
  public Type egressType(Scope scope) {
    // temp!
    return Types.VOID;
  }

  @Override
  public void emit(Emitter emitter) {
    emitter.writePlain(name);
    if (isMethod) {
      args.emit(emitter);
    }
  }

  @Override
  public String toString() {
    return "Call{" + name + (isMethod ? args.toString() : "") + "}";
  }

  @Override
  public String toSymbol() {
    return name + (isMethod ? Parser.stringify(args) : "");
  }
}
