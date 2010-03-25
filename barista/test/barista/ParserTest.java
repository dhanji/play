package barista;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Simple parser tests.
 */
public class ParserTest {

  @Test
  public final void integer() {
    List<Token> tokens = new Lexer("23").read();

    Assert.assertEquals(Arrays.asList(new Token("23", Token.Kind.INTEGER)), tokens);
  }

  @Test
  public final void integerDotInteger() {
    List<Token> tokens = new Lexer("23.23").read();


    Assert.assertEquals(Arrays.asList(
        new Token("23", Token.Kind.INTEGER),
        new Token(".", Token.Kind.DOT),
        new Token("23", Token.Kind.INTEGER)
        ), tokens);

  }

  @Test
  public final void integerDotIdent() {
    List<Token> tokens = new Lexer("23.more").read();


    Assert.assertEquals(Arrays.asList(
        new Token("23", Token.Kind.INTEGER),
        new Token(".", Token.Kind.DOT),
        new Token("more", Token.Kind.VAR_IDENT)
        ), tokens);

  }

  @Test
  public final void integerSpacePlusSpaceIdent() {
    List<Token> tokens = new Lexer("23 + more").read();

    Assert.assertEquals(Arrays.asList(
        new Token("23", Token.Kind.INTEGER),
        new Token("+", Token.Kind.PLUS),
        new Token("more", Token.Kind.VAR_IDENT)
        ), tokens);

  }

  @Test
  public final void integerDotIdentChain() {
    List<Token> tokens = new Lexer("23.more.34.less.then.1.1").read();


    Assert.assertEquals(Arrays.asList(
        new Token("23", Token.Kind.INTEGER),
        new Token(".", Token.Kind.DOT),
        new Token("more", Token.Kind.VAR_IDENT),
        new Token(".", Token.Kind.DOT),
        new Token("34", Token.Kind.INTEGER),
        new Token(".", Token.Kind.DOT),
        new Token("less", Token.Kind.VAR_IDENT),
        new Token(".", Token.Kind.DOT),
        new Token("then", Token.Kind.VAR_IDENT),
        new Token(".", Token.Kind.DOT),
        new Token("1", Token.Kind.INTEGER),
        new Token(".", Token.Kind.DOT),
        new Token("1", Token.Kind.INTEGER)
    ), tokens);

  }
}
