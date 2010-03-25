package barista;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic parser.
 */
public class Lexer {
  private final char[] text;

  public Lexer(String text) {
    this.text = text.toCharArray();
  }

  // current cursor position
  private int start = 0;
  private int index = 0;
  private List<Token> tokens = new ArrayList<Token>();

  // Lexer:
  public List<Token> read() {

    // we always just assume it's an integer
    Token.Kind kind = Token.Kind.INTEGER;

    for (;index < text.length; index++) {
      char c = text[index];

      // If whitespace, end token and keep going
      if (Character.isWhitespace(c)) {
        // end token.
        bakeToken(kind);
        continue;
      }

      Token.Kind possibleDelimiter = DELIMITERS[c];

      // Delimiters
      if (null != possibleDelimiter) {
        bakeToken(kind);
        index++;

        bakeToken(possibleDelimiter);

        // reset kind.
        kind = Token.Kind.INTEGER;
        continue;
      }

      // If not an integer, then treat as an identifier
      if (kind.equals(Token.Kind.INTEGER) && !Character.isDigit(c)) {
        kind = Token.Kind.VAR_IDENT;
      }

    }

    // last token.
    if (index > start) {
      bakeToken(kind);
    }

    return tokens;
  }

  private void bakeToken(Token.Kind kind) {
    Token token = Token.of(text, start, index, kind);

    // Null means this was not a worthy token (e.g. pure whitespace)
    if (null != token)
      tokens.add(token);

    // reset start marker to current index.
    start = index;
  }

  private static final Token.Kind[] DELIMITERS = new Token.Kind[256];

  static {
    DELIMITERS[':'] = Token.Kind.ASSIGN;
    DELIMITERS['('] = Token.Kind.LPAREN;
    DELIMITERS[')'] = Token.Kind.RPAREN;
    DELIMITERS['['] = Token.Kind.LBRACKET;
    DELIMITERS[']'] = Token.Kind.RBRACKET;
    DELIMITERS['{'] = Token.Kind.LBRACE;
    DELIMITERS['}'] = Token.Kind.RBRACE;
    DELIMITERS[','] = Token.Kind.COMMA;
    DELIMITERS['?'] = Token.Kind.EXISTS;
    DELIMITERS['.'] = Token.Kind.DOT;
  }
}
