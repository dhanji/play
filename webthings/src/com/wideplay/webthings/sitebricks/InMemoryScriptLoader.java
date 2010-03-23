package com.wideplay.webthings.sitebricks;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.apache.commons.io.IOUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.util.CompilerTools;
import org.mvel2.util.MethodStub;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
@Singleton
public class InMemoryScriptLoader implements ScriptLoader {
  private final Map<String, ExecutableScript> scripts = new MapMaker().makeMap();
  private final ServletContext context;

  /** ugh, but no other way to expose it to mvel scripts =( **/
  private static Injector injector;

  @Inject
  public InMemoryScriptLoader(ServletContext context, Injector injector) {
    this.context = context;
    InMemoryScriptLoader.injector = injector;
  }

  public ExecutableScript load(ScriptDescriptor name) {

    // Prepend with /
    InputStream scriptStream = context.getResourceAsStream("/WEB-INF/scripts/" + name.getScriptName());
    InputStream templateStream = context.getResourceAsStream("/" + name.getTemplateName());
    Preconditions.checkArgument(null != scriptStream, "Script was not found");
    Preconditions.checkArgument(null != templateStream, "Template was not found");

    internalLoad(name.getScriptName(), scriptStream, templateStream);

    return scripts.get(name.getScriptName());
  }

  private void internalLoad(String name, InputStream scriptStream, InputStream templateStream) {
    ParserContext ctx = new ParserContext();
    ctx.addImport("print", new MethodStub(InMemoryScriptLoader.class, "print"));
    ctx.addImport("global", new MethodStub(InMemoryScriptLoader.class, "global"));

    CompiledExpression script;
    String template = null;
    try {
      script = new ExpressionCompiler(IOUtils.toString(scriptStream), ctx)._compile();

      if (null != templateStream) {
        template = IOUtils.toString(templateStream);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Maybe we should guard this in prod mode as a cache.
    scripts.put(name, new ExecutableScript(script,
        CompilerTools.extractAllDeclaredFunctions(script), template));
  }

  public ExecutableScript load(String name) {

    InputStream scriptStream = context.getResourceAsStream("/WEB-INF/scripts/rpc/" + name);
    Preconditions.checkArgument(null != scriptStream, "Script was not found: " + name);
    internalLoad(name, scriptStream,  null);

    return scripts.get(name);
  }

  // MVEL shortcut for stdout
  public static void print(Object st) {
    System.out.println(st);
  }

  // MVEL shortcut for injector lookups
  public static <T> T global(Class<T> clazz) {
    return injector.getInstance(clazz);
  }
}
