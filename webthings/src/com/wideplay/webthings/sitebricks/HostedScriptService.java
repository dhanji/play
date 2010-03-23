package com.wideplay.webthings.sitebricks;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.mvel2.MVEL;
import org.mvel2.ast.Function;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * A general RESTful webservice that hosts other scripting platforms.
 *
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
@Service @Singleton
public class HostedScriptService {
  private final Provider<HttpServletRequest> request;
  private final Injector injector;
  private final ScriptLoader loader;
  private final ServletContext context;

  // Mapping of url -> script name
  private final ConcurrentMap<String, ScriptDescriptor> scripts = new MapMaker().makeMap();
  private final ConcurrentMap<String, String> rpcs = new MapMaker().makeMap();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject
  public HostedScriptService(ServletContext context,
                             Provider<HttpServletRequest> request,
                             Injector injector,
                             ScriptLoader loader) throws IOException, NoSuchMethodException {
    this.request = request;
    this.injector = injector;
    this.loader = loader;
    this.context = context;

    loadConfig();
  }

  @Get
  public Reply<?> dispatchGet(@Named("path") String path) {
    return dispatchMethod("get");
  }

  @Post
  public Reply<?> dispatchPost(@Named("path") String path) {
    return dispatchMethod("post");
  }

  private void loadConfig() {
    InputStream configStream = context.getResourceAsStream("/WEB-INF/site-engine.mvel");
    Preconditions.checkArgument(null != configStream, "Site config was not found");
    String config;
    try {
      config = IOUtils.toString(configStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Map<String, Object> ctx = new HashMap<String, Object>();
    MVEL.eval(config, ctx);

    // Dig out mappings
    @SuppressWarnings("unchecked")
    Map<String, List<String>> mappings = (Map<String, List<String>>) ctx.get("mappings");

    for (Map.Entry<String,List<String>> entry : mappings.entrySet()) {
      String url = entry.getKey();
      String scriptName = entry.getValue().get(0);
      String templateName = entry.getValue().get(1);

      scripts.put(url, new ScriptDescriptor(url, scriptName, templateName));
    }

    @SuppressWarnings("unchecked")
    Map<String, String> rpcs = (Map<String, String>) ctx.get("rpcs");

    for (Map.Entry<String, String> entry : rpcs.entrySet()) {
      String rpc = entry.getKey();
      String scriptName = entry.getValue();

      this.rpcs.put(rpc, scriptName);
    }
  }

  private Reply<?> dispatchMethod(String function) {

    HttpServletRequest request = this.request.get();
    if (null != request.getParameter("reload")) {
      System.out.println("reloading...");
      loadConfig();
    }

    //First see if we have a mapping for this url
    // strip trailing slash
    String uri = normalizeUri(request);
    ScriptDescriptor scriptName = scripts.get(uri);
    
    if (null == scriptName) {
      return Reply.NO_REPLY;
    }

    // Inject parameter map and evaluate script against it.
    Map<String, Object> context = new HashMap<String, Object>();

    @SuppressWarnings("unchecked")
    Map<String, String[]> rawParams = request.getParameterMap();

    // Transform the parameter map so it is easier to work with.
    Map<String, Object> params = new HashMap<String, Object>();
    for (Map.Entry<String, String[]> param : rawParams.entrySet()) {
      String[] value = param.getValue();
      if (value.length == 1) {
        params.put(param.getKey(), value[0]);
      } else if (value.length > 1) {
        params.put(param.getKey(), value);
      }
    }

    context.put("params", params);

    // Not sure if we should expose this
    context.put("request", request);

    try {
      MapVariableResolverFactory vars = new MapVariableResolverFactory(context);
      ExecutableScript script = loader.load(scriptName);

      // this sets up any globals floating around in the script
      MVEL.executeExpression(script.script, this, vars);

      // now we call get() with the context provided
      Function method = script.functions.get(function);
      if (null == method) {
        // TODO add no method status code to Sitebricks.
        Reply.with("There is no " + method + "() method handler!").error();
      }
      Object result = method
          .call(this, this, vars, new Object[]{});

      // Now eval it against the appropriate template
      return Reply.with(TemplateRuntime.eval(script.template, result))
                  .type("text/html");

    } catch (RuntimeException e) {

      // We still report errors
//      return Reply.with(e).error();
      throw e;
    }
  }

  private String normalizeUri(HttpServletRequest request) {
    String uri = request.getRequestURI();
    if (uri.length() > 1 && uri.endsWith("/")) {
      uri = uri.substring(0, uri.length() - 1);
    }
    return uri;
  }

  public Object invokeRpc(Map<String, Object> rpc) {
    String rpcName = (String) rpc.get("rpc");
    String scriptName = rpcs.get(rpcName);
    ExecutableScript script = loader.load(scriptName);

    Preconditions.checkArgument(null != script, "No such RPC: " + rpcName);

    // execute rpc.
    //noinspection ConstantConditions
    return MVEL.executeExpression(script.script, rpc);
  }
}
