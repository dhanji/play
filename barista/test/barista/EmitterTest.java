package barista;

import barista.ast.script.FunctionDecl;
import barista.ast.script.Unit;
import org.junit.Test;

/**
 * Tests emitting a reduced AST to Java source code.
 */
public class EmitterTest {

  @Test
  public final void simpleFunction() {
    String script = "func: ->\n  x + 1\n  x - 1\n\n\nmain: ->\n  print('hello')\n";

    Unit unit = new Parser(new Tokenizer(script).tokenize()).script();
    unit.reduceAll();

    FunctionDecl fn = unit.get("main");
    System.out.println(Parser.stringify(fn));

    System.out.println(new JavaEmitter("Default", unit).emit());
  }

}
