package barista.ast;

/**
 * A Type literal. Similar to XX.class in Java.
 */
public class TypeLiteral extends Node {
  private final String name;

  public TypeLiteral(String value) {
    name = value;
  }

  @Override
  public String toString() {
    return "TypeLiteral{" +
        "name='" + name + '\'' +
        '}';
  }

  @Override
  public String toSymbol() {
    return name;
  }
}
