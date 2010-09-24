package barista.type;

/**
 * Utility sets up universally available types in a top-level scope.
 */
public class UniversallyVisibleTypes {
  public static void populate(Scope scope) {
    scope.load(Types.INTEGER);
    scope.load(Types.STRING);
    scope.load(Types.VOID);

    scope.load(Types.LIST);
    scope.load(Types.MAP);
  }
}
