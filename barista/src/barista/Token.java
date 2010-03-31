package barista;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dhanji R. Prasanna
 */
public class Token {
  public final String value;
  public final Kind kind;

  public Token(String value, Kind kind) {
    this.value = value;
    this.kind = kind;
  }

  public static enum Kind {
    PRIVATE_FIELD,
    IDENT,
    INTEGER,
    DOT,

    PLUS,
    MINUS,
    DIVIDE,
    STAR,
    MODULUS,

    COLON,
    COMMA,


    ARROW,
    FAT_ARROW,
    EQUALS,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACKET,
    RBRACKET,

    // keywords
    CLASS,
    CONSTRUCTOR,
    OR,
    AND,
    NOT,
    IF,
    THEN,
    ELSE,
    UNLESS,

    WHEN,
    SWITCH,

    // specials
    EOL,
    INDENT;

    private static final Map<String, Kind> TOKEN_MAP = new HashMap<String, Kind>();

    static {
      // can we optimize with chars?
      TOKEN_MAP.put(".", DOT);
      TOKEN_MAP.put("+", PLUS);
      TOKEN_MAP.put("-", MINUS);
      TOKEN_MAP.put("/", DIVIDE);
      TOKEN_MAP.put("*", STAR);
      TOKEN_MAP.put("%", MODULUS);
      TOKEN_MAP.put(":", COLON);
      TOKEN_MAP.put(",", COMMA);
      TOKEN_MAP.put("->", ARROW);
      TOKEN_MAP.put("=>", FAT_ARROW);

      TOKEN_MAP.put("==", EQUALS);

      TOKEN_MAP.put("(", LPAREN);
      TOKEN_MAP.put(")", RPAREN);
      TOKEN_MAP.put("{", LBRACE);
      TOKEN_MAP.put("}", RBRACE);
      TOKEN_MAP.put("[", LBRACKET);
      TOKEN_MAP.put("]", RBRACKET);
      TOKEN_MAP.put("\n", EOL);

      TOKEN_MAP.put("if", IF);
      TOKEN_MAP.put("then", THEN);
      TOKEN_MAP.put("else", ELSE);
      TOKEN_MAP.put("when", WHEN);
      TOKEN_MAP.put("unless", UNLESS);
      TOKEN_MAP.put("constructor", CONSTRUCTOR);
      TOKEN_MAP.put("class", CLASS);


      TOKEN_MAP.put("||", OR);
      TOKEN_MAP.put("or", OR);
      TOKEN_MAP.put("&&", AND);
      TOKEN_MAP.put("and", AND);
      TOKEN_MAP.put("!", NOT);
      TOKEN_MAP.put("not", NOT);
    }

    /**
     * from token text, determines kind.
     */
    public static Kind determine(String value) {
      if (value.charAt(0) == '@')
        return PRIVATE_FIELD;

      Kind knownKind = TOKEN_MAP.get(value);
      if (null != knownKind)
        return knownKind;

      // integers
      if (value.matches("[0-9]+")) {
        return INTEGER;
      }

      return IDENT;
    }
  }

  @Override
  public String toString() {
    return "Token{" +
        "value='" + value + '\'' +
        ", kind=" + kind +
        '}';
  }
}
