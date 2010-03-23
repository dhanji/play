package com.wideplay.webthings.sitebricks;

import org.mvel2.ast.Function;
import org.mvel2.compiler.CompiledExpression;
import sun.security.x509.CRLExtensions;

import java.util.Map;

/**
 * Value object represents a script and its execution.
 * For mvel.
 *
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class ExecutableScript {
  final CompiledExpression script;
  final Map<String, Function> functions;

  final String template;

  public ExecutableScript(CompiledExpression script,
                          Map<String, Function> functions,
                          String template) {
    this.script = script;
    this.functions = functions;
    this.template = template;
  }
}
