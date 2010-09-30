package barista.type;

import javassist.CannotCompileException;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
public class Errors {
  private final Set<CompileError> errors = new LinkedHashSet<CompileError>();

  public void unknownSymbol(String var) {
    // TODO maybe a fuzzy match and suggest?
    errors.add(new CompileError("Use of unknown symbol " + var));
  }

  public void check(Type expected, Type actual, String message) {
    if (!expected.equals(actual)) {
      errors.add(new CompileError("Type mismatch, expected " + expected.name() + " but was "
          + actual.name() + " in " + message + "."));
    }
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public void exception(Exception e) {
    errors.add(new CompileError(e.getMessage()));
  }

  public void generic(String message) {
    errors.add(new CompileError(message));
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Compile errors found:\n");
    int i = 1;
    for (CompileError error : errors) {
      builder.append(i++);
      builder.append(") ");
      builder.append(error.message());
      builder.append("\n\n");
    }

    return builder.toString();
  }
}
