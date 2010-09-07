package barista;

import javassist.ClassPool;

/**
 * Converts parsed, type-solved, emitted code to Java classes.
 */
public class CompilingInterpreter {
  private final String rawEmittedJava;
  private final ClassPool pool = ClassPool.getDefault();

  public CompilingInterpreter(String rawEmittedJava) {
    this.rawEmittedJava = rawEmittedJava;

  }

  public void run() {
  }

}
