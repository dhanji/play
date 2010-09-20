package barista;

import java.util.Collection;

/**
 * Represents a compilation or type error.
 */
public class CompileError {
  private final String message;

  public CompileError(String message) {
    this.message = message;
  }
  
  public static String toString(Collection<CompileError> errors) {
    StringBuilder builder = new StringBuilder("Compile errors found:\n");
    int i = 1;
    for (CompileError error : errors) {
      builder.append(i++);
      builder.append(") ");
      builder.append(error.message);
      builder.append("\n\n");
    }

    return builder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CompileError that = (CompileError) o;

    if (message != null ? !message.equals(that.message) : that.message != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return message != null ? message.hashCode() : 0;
  }
}
