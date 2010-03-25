package barista;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a lexing token.
 */
public class Token {
  public final String value;
  public final Kind kind;

  public Token(String value, Kind kind) {
    this.value = value;
    this.kind = kind;
  }

  private static final Map<String, Kind> KINDS = new HashMap<String, Kind>();

  static {
    KINDS.put("+", Kind.PLUS);
  }

  public static Token of(char[] text, int start, int end, Kind kind) {
    String s = new String(text, start, end - start).trim();
    if (s.isEmpty())
      return null;


    // See if we can refine this a bit...
    if (kind == Kind.VAR_IDENT) {
      Token.Kind refine = KINDS.get(s);
      if (null != refine)
        kind = refine;
    }

    return new Token(s, kind);
  }

  public static enum Kind {

    // literals
    INTEGER,
    STRING,
    HEREDOC,

    ASSIGN,
    LPAREN, RPAREN,
    LBRACKET, RBRACKET,
    LBRACE, RBRACE,
    EQUALS,
    ARROW,                  // function decl ->
    COMMA,
    ELLIPSIS,               //... for varargs decls
    RANGE,                  //.. for ranges
    DOT,                    //. for dereferencing

    // Idents
    VAR_IDENT,
    PRIVATE_FIELD_IDENT,    // @field
    THUNK_IDENT,            // thunk()

    // keywords
    IF, THEN, ELSE, UNLESS,
    TRY, CATCH, FINALLY,
    FOR, IN, BY, OF,
    WHILE,
    AND, OR, NOT, ISNT,
    TRUE, FALSE,            // aliased to yes/no, true/false, on/off

    // OO keyword set
    NEW,
    CLASS, EXTENDS, SUPER, THIS,
    CONSTRUCTOR,

    EXISTS,                 // ? postfix operator


    //specials
    EOL,
    EOF,


    // arithmetic operators
    PLUS, DIVIDE, MULTIPLY, MINUS,

  }

  @Override
  public String toString() {
    return "Token{" +
        "value='" + value + '\'' +
        ", kind=" + kind +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    Token that = (Token)o;

    return that.kind.equals(this.kind) && that.value.equals(this.value);
  }

  @Override
  public int hashCode() {
    int result = value.hashCode();
    result = 31 * result + (kind.hashCode());
    return result;
  }
}
