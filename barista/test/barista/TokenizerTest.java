package barista;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dhanji R. Prasanna
 */
public class TokenizerTest {

  @Test
  public final void singleLineStatements() {
    compare("\" hi there! \" . to_s - 1", "\" hi there! \".to_s - 1");
    compare("++ 1", "++1");
    compare("1 + 2", "1 +    2");
    compare("x . y + 1 --", "x.y + 1--");
    compare("1 + 2 . double - 2 . 23", "1 +    2.double -2.23");
    compare("1 + 2 . double - 2 . 23 / x ++", "1 +    2.double -2.23 / x++");
    compare("func : ( x , y , z ) -> 'hi'", "func: (x, y, z) -> 'hi'");
    compare("a : ++ 1", "a: ++1");
  }

  @Test
  public final void simpleMultiLineStatements() {
    compare("func : ( ) -> \n 'hi'", "func: () -> \n 'hi'");
    compare("func : ( ) -> \n 'hi'", "func: () ->\n 'hi'");
//    compare("func : ( ) -> \n 'hi'", "func: () ->\n 'hi'");
  }

  private static void compare(String expected, String input) {
    Assert.assertEquals(expected, Tokenizer.detokenize(new Tokenizer(input).tokenize()));
  }
}
