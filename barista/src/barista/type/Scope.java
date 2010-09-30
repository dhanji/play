package barista.type;

import barista.ast.Variable;
import barista.ast.script.FunctionDecl;

import java.util.List;

/**
 * A lexical scope in jade source code.
 *
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public interface Scope {
  void load(Variable v);

  void load(FunctionDecl func);

  void load(Type type);

  Variable getVariable(String name);

  FunctionDecl getFunction(String name);

  Type getType(String name);

  Scope parent();

  Errors errors();

  void witness(FunctionDecl functionDecl, List<Type> bound, Type inferred);


  List<BasicScope.Witness> getWitnesses();

  class Witness {
    public final FunctionDecl functionDecl;
    public final Type returnType;
    public final List<Type> argumentTypes;

    public Witness(FunctionDecl functionDecl, List<Type> argumentTypes, Type returnType) {
      this.functionDecl = functionDecl;
      this.argumentTypes = argumentTypes;
      this.returnType = returnType;
    }
  }
}
