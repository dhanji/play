package barista;

import barista.ast.script.FunctionDecl;
import barista.ast.script.Unit;

/**
 * Tests emitting a reduced AST to Java source code.
 */
public class EmitterTest {

//  @Test
  public final void simpleFunction() {
    String script = "func: ->\n  x + 1\n  x - 1\n  print('func running!' + x)\n\nmain: ->\n  func()\n  print('hi')\n";

    Unit unit = new Parser(new Tokenizer(script).tokenize()).script();
    unit.reduceAll();

    FunctionDecl fn = unit.get("main");
    System.out.println(Parser.stringify(fn));

    new CompilingInterpreter(new JavaEmitter("Default", unit).emit()).run();
  }
}
