package com.wideplay.webthings.sitebricks;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class WebthingsStarter extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new HostedModule());
  }
}
