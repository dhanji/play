package barista;

import barista.ast.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Takes the tokenized form of a raw string and converts it
 * to a CoffeeScript parse tree (an optimized form of its AST).
 *
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class Parser {
  private final Token[] tokens;
  private Node current = new UnaryOp();
  private int start = 0, i = 0;

  private static final Set<Token.Kind> RIGHT_ASSOCIATIVE = new HashSet<Token.Kind>();
  private static final Set<Token.Kind> LEFT_ASSOCIATIVE = new HashSet<Token.Kind>();

  static {
    RIGHT_ASSOCIATIVE.add(Token.Kind.PLUS);
    RIGHT_ASSOCIATIVE.add(Token.Kind.MINUS);
    RIGHT_ASSOCIATIVE.add(Token.Kind.DIVIDE);
    RIGHT_ASSOCIATIVE.add(Token.Kind.STAR);

    LEFT_ASSOCIATIVE.add(Token.Kind.DOT);
  }

  public Parser(Token[] tokens) {
    this.tokens = tokens;
  }

  public void parse() {
    current = expression(current);
    if (current instanceof UnaryOp){
      // unwrap
      current = current.children().get(0);
    }
  }

  /**
   * expr := (literal | variable) ( operator expression )? EOL
   */
  public Node expression(Node current) {
    Node parsed = null;
    for (i = start; i < tokens.length; i++) {
      Token token = tokens[i];

      Node term = term(token);
      Token oneAhead = lookAhead(1), twoAhead = lookAhead(2);
      if (null != term)
        parsed = term;
      else {
        twoAhead = oneAhead;
        oneAhead = token;
      }

      // If we found something try and look for a right associative operator.
      if (null != parsed && isRightAssociative(oneAhead)) {
        // take whatever's in the statement so far and associate it to the right ->
        BinaryOp op = new BinaryOp(oneAhead);

        op.add(parsed);

        // move cursor forward
        start = i + 1;

        expression(op);
        parsed = op;
        
      } else if (null != parsed && isLeftAssociative(oneAhead)) {
        
        // needs to be a while loop that slurps entire dot chain...
        BinaryOp op = new BinaryOp(oneAhead);

        op.add(parsed);

        // instead of recursively parsing forward, just slurp the next token up.
        if (null == twoAhead)
          throw new RuntimeException("parse error, expected token after " + op.toSymbol());

        op.add(new Variable(twoAhead.value));
//        current.add(op);
        parsed = op;

        start = i += 2; // chew up two tokens.
      }
    }

//    if (null != parsed)
      current.add(parsed);
    return current;
  }

  private Node term(Token token) {
    Node parsed = null;
    if (Token.Kind.INTEGER == token.kind) {
      parsed = new IntLiteral(token.value);
    } else if (Token.Kind.IDENT == token.kind) {
      parsed = new Variable(token.value);
    } else if (Token.Kind.STRING == token.kind) {
      parsed = new StringLiteral(token.value);
    }
    return parsed;
  }

  private Token lookAhead(int offset) {
    return (i + offset < tokens.length) ? tokens[i + offset] : null;
  }

  public Node ast() {
    return current;
  }

  private static boolean isLeftAssociative(Token token) {
    return null != token && LEFT_ASSOCIATIVE.contains(token.kind);
  }

  private static boolean isRightAssociative(Token token) {
    return null != token && RIGHT_ASSOCIATIVE.contains(token.kind);
  }

  /**
   * recursively walks a parse tree and turns it into a symbolic form
   * that is test-readable.
   */
  public static String stringify(Node tree) {
    StringBuilder builder = new StringBuilder();

    boolean shouldWrapInList = hasChildren(tree);
    if (shouldWrapInList)
      builder.append('(');
    builder.append(tree.toSymbol());

    if (shouldWrapInList)
      builder.append(' ');

    for (Node child : tree.children()) {
      String s = stringify(child);
      if (s.length() == 0)
        continue;

      builder.append(s);
      builder.append(' ');
    }

    // chew last ' '

    if (shouldWrapInList) {
      builder.deleteCharAt(builder.length() - 1);
      builder.append(')');
    }

    return builder.toString();
  }

  private static boolean hasChildren(Node tree) {
    return (null != tree.children()) && !tree.children().isEmpty();
  }
}
