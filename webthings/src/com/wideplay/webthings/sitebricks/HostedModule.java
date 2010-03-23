package com.wideplay.webthings.sitebricks;

import com.google.sitebricks.SitebricksModule;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class HostedModule extends SitebricksModule {
  @Override
  protected void configureSitebricks() {
    // Hack to make sitebricks use this for all urls
    at("/").serve(HostedScriptService.class);
    at("/:path").serve(HostedScriptService.class);
  }
}
