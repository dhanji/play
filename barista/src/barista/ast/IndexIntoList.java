package barista.ast;

import barista.JadeCompiler;
import barista.Parser;

/**
 * An array dereference. Can also be an array slice. For example:
 *
 * arr[x]
 *
 * or:
 *
 * arr[1..5]
 *
 * or:
 *
 * arr[a..b]
 *
 * It may also be a partial slice:
 *
 * arr[0..]
 *
 * or:
 *
 * arr[..5]
 *
 */
public class IndexIntoList extends Node {
  private Node from;
  private final boolean slice;
  private Node to;

  public IndexIntoList(Node from, boolean slice, Node to) {
    this.from = from;
    this.slice = slice;
    this.to = to;
  }

  public Node from() {
    return from;
  }

  public void from(Node from) {
    this.from = from;
  }

  public Node to() {
    return to;
  }

  public void to(Node to) {
    this.to = to;
  }

  @Override
  public void emit(JadeCompiler jadeCompiler) {
    if (null == from && null == to) {
      jadeCompiler.errors().generic("Invalid list index range specified");
      return;
    }

    if (slice) {
      jadeCompiler.writePlain("subList(");
      if (null == from) {
        jadeCompiler.writePlain("0");
      } else {
        from.emit(jadeCompiler);
      }
      jadeCompiler.writePlain(", ");
      if (null == to) {
      } else {
        to.emit(jadeCompiler);
      }
      jadeCompiler.writePlain(")");
    } else {
      jadeCompiler.writePlain("get(");
      from.emit(jadeCompiler);
      jadeCompiler.writePlain(")");
    }
  }

  @Override
  public String toSymbol() {
    return "["
        + (from == null ? "" : Parser.stringify(from))
        + (slice ? ".." : "")
        + (to == null ? "" : Parser.stringify(to))
        + "]";
  }
}
