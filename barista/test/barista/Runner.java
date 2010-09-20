package barista;

/**
 * Use to run script files
 */
public class Runner {
  public static void main(String...args) {
    CompilingInterpreter.execute("test/barista/scripts/math_test.jade");
  }
}
