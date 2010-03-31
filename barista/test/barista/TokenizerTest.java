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
  public final void singleLineStatementsWithComments() {
    compare("\" hi there! \" . to_s - 1", "\" hi there! \".to_s - 1 # yoyoy");
    compare("1 + 2", "1 +    2 # + 2");
    compare("x . y + 1 --", "x.y + 1--");
    compare("func : ( x , y , z ) -> \"hi #d\"", "func: (x, y, z) -> \"hi #d\"");
    compare("a : ++ 1", "a: ++1 # pound");
    compare("func : ( x , y , z ) -> 'hi #d'", "func: (x, y, z) -> 'hi #d'");
    compare("", "# + 2");
    compare("", " # soikdokdpoaksd### 3aoskdoaksd\n ###");
  }

  @Test
  public final void simpleMultilineStatements() {
    compare("func : ( ) -> \n 'hi'", "func: () -> \n 'hi'");
    compare("func : ( ) -> \n 'hi'", "func: () ->\n 'hi'");
    compare("func : ( x , y ) -> \n 'hi' \n 2", "func: (x,y) ->\n 'hi'\n 2");
    compare("func : -> \n 'hi'", "func: -> \n 'hi'");
  }

  @Test
  public final void compoundMultilineStatements() {
    compare("class Me \n talk : -> \n 'hi'", "class Me \n  talk: ->\n  'hi'");
    compare("class Me \n constructor : -> \n @my : your \n talk : -> \n 'hi'",
            "class Me\n  constructor: ->\n  @my: your\n  talk : -> \n 'hi'");
  }

  private static void compare(String expected, String input) {
    Assert.assertEquals(expected, Tokenizer.detokenize(new Tokenizer(input).tokenize()));
  }
}
