package barista;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class ParserTest {

  @Test
  public final void simpleExpr() {
    compare("(+ 1 2)", "1 + 2");
    compare("(+ 1 (+ 2 3))", "1 + 2 + 3");
    compare("(+ 1 (+ 2 (. 3 triple)))", "1 + 2 + 3.triple");

    compare("(- (. \" hi there! \" to_s) 1)", "\" hi there! \".to_s - 1 # yoyoy");

    compare("(. (. a b) c)", "a.b.c");
    compare("(. (. (. a b) c) d)", "a.b.c.d");
  }

  static void compare(String expected, String input) {
    Parser parser = new Parser(new Tokenizer(input).tokenize());
    parser.parse();

    System.out.println("Parse Tree--\n" + parser.ast());
    System.out.println("Parse S-Expr--\n" + Parser.stringify(parser.ast()));
    Assert.assertEquals(expected, Parser.stringify(parser.ast()));
  }
}
