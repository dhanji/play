package barista.ast;

import barista.JadeCompiler;
import barista.Parser;
import barista.ast.script.FunctionDecl;
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
  private static final String PRINT = "System.out.println";

  public Call(String name, boolean method, Arglist args) {
    // HACK! rewrites print calls to Java sout
    this.name = "print".equals(name) ? PRINT : name;
    this.isMethod = method;
    this.args = args;
  }

  public Arglist args() {
    return args;
  }

  @Override
  public Type egressType(Scope scope) {
    // MAJOR HACK(dhanji): Special case print for now.
    if (PRINT.equals(name)) {
      // Because we short-circuit the print() case, we need to probe the ingress
      // types of the (single) argument. This triggers the witnessing of called
      // functions that are polymorphic in the argument expression.
      // In other words, we need to exercise the argument types in order to generate
      // overloads for any unbound functions.
      assert args.children().size() == 1;
      args.children().get(0).egressType(scope);

      return Types.VOID;
    }

    FunctionDecl function = scope.getFunction(name);
    if (null == function) {
      scope.errors().unknownFunction(name);
      return Types.VOID;
    }
    return function.inferType(scope, args);
  }

  @Override
  public void emit(JadeCompiler jadeCompiler) {
    jadeCompiler.write(name);
    if (isMethod) {
      args.emit(jadeCompiler);
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
