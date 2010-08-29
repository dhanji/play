package barista;

import barista.ast.*;
import barista.ast.script.ModuleDecl;
import barista.ast.script.RequireDecl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Takes the tokenized form of a raw string and converts it
 * to a CoffeeScript parse tree (an optimized form of its AST).
 *
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class Parser {
  private final List<Token> tokens;
  private Node last = null;
  private int i = 0;

  private static final Set<Token.Kind> RIGHT_ASSOCIATIVE = new HashSet<Token.Kind>();
  private static final Set<Token.Kind> LEFT_ASSOCIATIVE = new HashSet<Token.Kind>();

  static {
    RIGHT_ASSOCIATIVE.add(Token.Kind.PLUS);
    RIGHT_ASSOCIATIVE.add(Token.Kind.MINUS);
    RIGHT_ASSOCIATIVE.add(Token.Kind.DIVIDE);
    RIGHT_ASSOCIATIVE.add(Token.Kind.STAR);

    RIGHT_ASSOCIATIVE.add(Token.Kind.AND);
    RIGHT_ASSOCIATIVE.add(Token.Kind.OR);
    RIGHT_ASSOCIATIVE.add(Token.Kind.EQUALS);
    RIGHT_ASSOCIATIVE.add(Token.Kind.LEQ);
    RIGHT_ASSOCIATIVE.add(Token.Kind.GEQ);
    RIGHT_ASSOCIATIVE.add(Token.Kind.LESSER);
    RIGHT_ASSOCIATIVE.add(Token.Kind.GREATER);

    LEFT_ASSOCIATIVE.add(Token.Kind.DOT);
  }

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   *
   * if := IF computation
   *
   * assign := computation ASSIGN computation
   *
   * computation := chain (op chain)+
   * chain := term call*
   *
   * call := DOT IDENT (LPAREN RPAREN)?
   *
   * term := (literal | variable)
   * literal := (regex | string | number)
   * variable := IDENT
   *
   *
   * Examples
   * --------
   *
   * (assign)
   *
   * x = "hi".tos().tos()
   * x = 1
   *
   * (computation)
   *
   * 1 + 2
   * 1 + 2 - 3 * 4
   * 1.int + 2.y() - 3.a.b * 4
   *
   * --------------------
   *
   * parse := module | require | line
   *
   */
  public Node parse() {
    Node parsed = require();
    if (null == parsed) {
      parsed = module();
    }
    if (null == parsed) {
      parsed = line();
    }

    return last = parsed;
  }

  /*** Class parsing rules ***/

  /**
   * require := REQUIRE IDENT (DOT IDENT)* EOL
   */
  private Node require() {
    if (match(Token.Kind.REQUIRE) == null) {
      return null;
    }

    List<Token> module = match(Token.Kind.IDENT);
    if (null == module) {
      throw new RuntimeException("Expected module identifier after 'require'");
    }

    List<String> requires = new ArrayList<String>();
    requires.add(module.get(0).value);

    while (match(Token.Kind.DOT) != null) {
      module = match(Token.Kind.IDENT);
      if (null == module) {
        throw new RuntimeException("Expected module identifier after '.'");
      }

      requires.add(module.get(0).value);
    }

    if (match(Token.Kind.EOL) == null) {
      throw new RuntimeException("Expected newline after require declaration");
    }

    return new RequireDecl(requires);
  }

  /**
   * module := MODULE IDENT (DOT IDENT)* EOL
   */
  private Node module() {
    if (match(Token.Kind.MODULE) == null) {
      return null;
    }

    List<Token> module = match(Token.Kind.IDENT);
    if (null == module) {
      throw new RuntimeException("Expected module identifier after 'require'");
    }

    List<String> modules = new ArrayList<String>();
    modules.add(module.get(0).value);

    while (match(Token.Kind.DOT) != null) {
      module = match(Token.Kind.IDENT);
      if (null == module) {
        throw new RuntimeException("Expected module identifier after '.'");
      }

      modules.add(module.get(0).value);
    }

    if (match(Token.Kind.EOL) == null) {
      throw new RuntimeException("Expected newline after require declaration");
    }

    return new ModuleDecl(modules);
  }


  /*** In-function instruction parsing rules ***/

  /**
   * parse := assign | statement
   */
  private Node line() {
    Node parsed = assign();
    if (null == parsed) {
      parsed = statement();
    }
    return parsed;
  }

  /**
   * Any non-assignment, single-evaluative statement. In other words the result produces
   * a single value that is not an assingment.
   *
   * Keep in mind that this MUST complement the assign rule, and not step on its toes
   * (under NO circumstance should computation be the first segment of this rule).
   *
   * statement := (comprehension | IF computation)
   */
  private Node statement() {
    Node comprehension = comprehension();
    if (null != comprehension) {
      return comprehension;
    }

    if (match(Token.Kind.IF) == null) {
      return null;
    }

    // Must be an if.
    Node ifPart = computation();
    if (null == ifPart) {
      throw new RuntimeException("Expected boolean expression after IF");
    }

    if (match(Token.Kind.THEN) == null) {
      throw new RuntimeException("IF missing THEN clause");
    }

    // TODO(dhanji): Refactor to multiline block when we support that.
    Node thenPart = line();
    if (null == thenPart) {
      throw new RuntimeException("Expected statement after THEN");
    }

    if (match(Token.Kind.ELSE) == null) {
      return new IfStatement(ifPart, thenPart);
    }
    Node elsePart = line();
    if (null == elsePart) {
      throw new RuntimeException("Expected statement after ELSE");
    }

    return new IfStatement(ifPart, thenPart, elsePart);
  }

  /**
   * This is really both "free standing expression" and "assignment".
   *
   * assign := computation
   *      (ASSIGN
   *          (computation (IF computation | comprehension)?)
   *          | (IF computation THEN computation ELSE computation)
   *          )?
   */
  private Node assign() {
    Node left = computation();
    if (null == left) {
      return null;
    }

    if (match(Token.Kind.ASSIGN) == null) {
      return left;
    }

    // Ternary operator if-then-else
    Node ifThenElse = ternaryIf();
    if (null != ifThenElse) {
      return new Assignment().add(left).add(ifThenElse);
    }

    // OTHERWISE-- continue processing a normal assignment.
    Node right = computation();
    if (null == right) {
      // TODO syntax error, dangling =
      return null;
    }

    // Is this a conditional assignment?
    Node condition = null;
    if (match(Token.Kind.IF) != null) {
      condition = computation();
    } else {
       // Is this a list comprehension?
      Node comprehension = comprehension();
      if (null != comprehension) {
        return new Assignment().add(left).add(comprehension);
      }
    }

    return new Assignment(condition).add(left).add(right);
  }

  /**
   * Ternary operator, like Java's ?:
   *
   *  ternaryIf := IF computation then computation else computation
   */
  private Node ternaryIf() {
    if (match(Token.Kind.IF) != null) {
      Node ifPart = computation();
      if (match(Token.Kind.THEN) == null) {
        throw new RuntimeException("IF with missing THEN");
      }

      Node thenPart = computation();
      if (match(Token.Kind.ELSE) == null) {
        throw new RuntimeException("IF/THEN with missing ELSE");
      }

      Node elsePart = computation();

      return new TernaryExpression()
          .add(ifPart)
          .add(thenPart)
          .add(elsePart);
    }

    return null;
  }

  /**
   * comprehension := FOR variable IN computation (AND computation)?
   */
  private Node comprehension() {
    if (match(Token.Kind.FOR) == null) {
      return null;
    }

    Node variable = variable();
    if (null == variable) {
      throw new RuntimeException("Expected identifier");
    }

    if (match(Token.Kind.IN) == null) {
      throw new RuntimeException("Expected 'in' after identifier");
    }

    Node inList = computation();
    if (null == inList) {
      throw new RuntimeException("Expected expression after 'in' in comprehension");
    }

    if (match(Token.Kind.IF) == null) {
      return new Comprehension(variable, inList, null);
    }

    Node filter = computation();
    if (filter == null) {
      throw new RuntimeException("Expected expression after 'and' in comprehension");
    }

    return new Comprehension(variable, inList, filter);
  }

  /**
   * group := LPAREN computation RPAREN
   */
  private Node group() {
    if (match(Token.Kind.LPAREN) == null) {
      return null;
    }

    Node computation = computation();
    if (null == computation) {
      throw new RuntimeException("Expected expression after '('");
    }

    if (match(Token.Kind.RPAREN) == null) {
      throw new RuntimeException("Expected ')'");
    }

    return computation;
  }

  /**
   * computation := (group | chain) ( (rightOp (group | chain)) )*
   */
  public Node computation() {
    Node node = group();
    if (node == null) {
      node = chain();
    }

    // Production failed.
    if (null == node) {
      return null;
    }

    Computation computation = new Computation();
    computation.add(node);

    Node rightOp;
    Node operand;
    while ((rightOp = rightOp()) != null) {
      operand = group();
      if (null == operand) {
        operand = chain();
      }
      if (null == operand) {
        break;
      }

      rightOp.add(operand);
      computation.add(rightOp);
    }

    return computation;
  }

  /**
   * chain := listOrMapDef | ternaryIf | (term  arglist? (call | indexIntoList)*)
   */
  private Node chain() {
    Node node = listOrMapDef();

    // If not a list, maybe a ternary if?
    if (null == node) {
      node = ternaryIf();
    }

    // If not a ternary if, maybe a term?
    if (null != node) {
      return node;
    }  else {
      node = term();
    }

    // Production failed.
    if (null == node) {
      return null;
    }

    // If args exist, then we should turn this simple term into a free method call.
    Arglist args = arglist();
    if (null != args && node instanceof Variable) {
      node = new Call(((Variable)node).name, true, args);
    }

    CallChain chain = new CallChain();
    chain.add(node);

    Node call, indexIntoList = null;
    while ( (call = call()) != null || (indexIntoList = indexIntoList()) != null ) {
      chain.add(call != null ? call : indexIntoList);
    }

    return chain;
  }

  /**
   * arglist := LPAREN (computation (COMMA computation)*)? RPAREN
   */
  private Arglist arglist() {
    // Test if there is a leading paren.
    List<Token> parenthetical = match(Token.Kind.LPAREN);
    boolean isParenthetical = (null != parenthetical);
    boolean isPositional = true;

    // Slurp arguments while commas exist.
    Arglist arglist = null;
    if (isParenthetical) {

      // See if this may be a named-arg invocation.
      List<Token> named = match(Token.Kind.IDENT, Token.Kind.ASSIGN);
      isPositional = (null == named);

      arglist = new Arglist(isPositional);
      Node arg = computation();
      if (null != arg) {

        // If this is a named arg, wrap it in a name.
        if (isPositional) {
          arglist.add(arg);
        } else {
          arglist.add(new Arglist.NamedArg(named.get(0).value, arg));
        }
      }
    }

    // Rest of argument list, comma separated.
    while (isParenthetical && match(Token.Kind.COMMA) != null) {
      List<Token> named = null;
      if (!isPositional) {
        named = match(Token.Kind.IDENT, Token.Kind.ASSIGN);
        if (null == named) {
          throw new RuntimeException("Cannot mix named and position arguments in a function call");
        }
      }

      Node arg = computation();
      if (null == arg) {
        throw new RuntimeException("Expected expression after ','");
      }

      if (isPositional) {
        arglist.add(arg);
      } else {
        arglist.add(new Arglist.NamedArg(named.get(0).value, arg));
      }
    }

    // Ensure the method invocation is properly closed.
    if (isParenthetical && match(Token.Kind.RPAREN) == null) {
      throw new RuntimeException("Expected ')' at end of argument list");
    }

    return arglist;
  }

  /**
   * An array deref.
   *
   * indexIntoList := LBRACKET (computation | computation? DOT DOT computation?)? RBRACKET
   */
  private Node indexIntoList() {
    if (match(Token.Kind.LBRACKET) == null) {
      return null;
    }

    Node index = computation();

    // This is a list slice with a range specifier.
    Node to = null;
    boolean slice = false;
    if (match(Token.Kind.DOT) != null) {
      if (match(Token.Kind.DOT) == null) {
        throw new RuntimeException("Syntax error, range specifier incomplete. Expected '..'");
      }

      slice = true;
      to = computation();
    } else if (index == null) {
      throw new RuntimeException("Expected symbol or '..' list slice operator.");
    }

    if (match(Token.Kind.RBRACKET) == null) {
      throw new RuntimeException("Expected ]");
    }

    return new IndexIntoList(index, slice, to);
  }

  /**
   * Inline list/map definition.
   *
   * listOrMapDef :=
   *      LBRACKET
   *          (computation
   *              ((COMMA computation)* | computation? DOT DOT computation?))
   *          |
   *          (computation HASHROCKET computation
   *              (COMMA computation HASHROCKET computation)*)
   *          |
   *          HASHROCKET
   *      RBRACKET
   */
  private Node listOrMapDef() {
    if (match(Token.Kind.LBRACKET) == null) {
      return null;
    }

    Node index = computation();

    Node list = new InlineListDef();
    if (null != index) {
      boolean isMap = match(Token.Kind.HASHROCKET) != null;
      if (isMap) {
        list = new InlineMapDef();

        // This map will be stored as a list of alternating keys/values (in pairs).
        list.add(index);
        Node value = computation();
        if (null == value) {
          throw new RuntimeException("Expected expression after '=>'");
        }
        list.add(value);
      } else {
        list.add(index);
      }

      // Slurp up all list or map argument values as a comma-separated sequence.
      while (match(Token.Kind.COMMA) != null) {
        Node listElement = computation();
        if (null == listElement) {
          throw new RuntimeException("Expected expression after ','");
        }

        list.add(listElement);

        // If the first index contained a hashrocket, then this is a map.
        if (isMap) {
          if (null == match(Token.Kind.HASHROCKET)) {
            throw new RuntimeException("Expected '=>' after key");
          }
          
          Node value = computation();
          if (null == value) {
            throw new RuntimeException("Expected expression after '=>'");
          }
          list.add(value);
        }
      }


      // OTHERWISE---
      // This is a list slice with a range specifier.
      Node to;
      boolean slice;
      if (match(Token.Kind.DOT) != null) {
        if (match(Token.Kind.DOT) == null) {
          throw new RuntimeException("Syntax error, range specifier incomplete. Expected '..'");
        }

        slice = true;
        to = computation();
        list = new ListRange(index, slice, to);
      }
    }

    // Is there a hashrocket?
    if (match(Token.Kind.HASHROCKET) != null) {
      // Otherwise this is an empty hashmap.
      list = new InlineMapDef();
    }
    if (match(Token.Kind.RBRACKET) == null) {
      throw new RuntimeException("Expected ]");
    }

    return list;
  }

  /**
   * A method call production rule.
   *
   * call := DOT IDENT arglist?
   */
  private Node call() {
    List<Token> call = match(Token.Kind.DOT, Token.Kind.IDENT);

    // Production failed.
    if (null == call) {
      return null;
    }

    Arglist arglist = arglist();

    // Use the ident as name, and it is a method if there are () at end.
    return new Call(call.get(1).value, null != arglist, arglist);
  }

  /**
   * term := (literal | variable | field)
   */
  private Node term() {
    Node term = literal();

    if (null == term) {
      term = variable();
    }

    if (null == term) {
      term = field();
    }

    return term;
  }

  /**
   * (lexer super rule)
   * literal := string | regex | integer | decimal
   */
  private Node literal() {
    Token token = anyOf(Token.Kind.STRING, Token.Kind.INTEGER, Token.Kind.REGEX, Token.Kind.TYPE_IDENT);
    if (null == token) {
      return null;
    }
    switch (token.kind) {
      case INTEGER:
        return new IntLiteral(token.value);
      case STRING:
        return new StringLiteral(token.value);
      case TYPE_IDENT:
        return new TypeLiteral(token.value);
      case REGEX:
        return null; // TODO fix.
    }
    return null;
  }

  private Node variable() {
    List<Token> var = match(Token.Kind.IDENT);
    return (null != var) ? new Variable(var.get(0).value) : null;
  }

  private Node field() {
    List<Token> var = match(Token.Kind.PRIVATE_FIELD);
    return (null != var) ? new PrivateField(var.get(0).value) : null;
  }


  /**
   * Right associative operator (see Token.Kind).
   */
  private Node rightOp() {
    if (i >= tokens.size()) {
      return null;
    }
    Token token = tokens.get(i);
    if (isRightAssociative(token)) {
      i++;
      return new BinaryOp(token);
    }

    // No right associative op found.
    return null;
  }

  // Production tools.
  private Token anyOf(Token.Kind... ident) {
    for (Token.Kind kind : ident) {
      Token token = tokens.get(i);
      if (kind == token.kind) {
        i++;
        return token;
      }
    }

    // No match =(
    return null;
  }

  private List<Token> match(Token.Kind... ident) {
    int cursor = i;
    for (Token.Kind kind : ident) {

      // What we want is more than the size of the token stream.
      if (cursor >= tokens.size()) {
        return null;
      }

      Token token = tokens.get(cursor);
      if (token.kind != kind) {
        return null;
      }

      cursor++;
    }

    // Forward cursor in token stream to match point.
    int start = i;
    i = cursor;
    return tokens.subList(start, i);
  }

  public Node ast() {
    return last;
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
