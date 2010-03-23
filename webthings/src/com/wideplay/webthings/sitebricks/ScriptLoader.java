package com.wideplay.webthings.sitebricks;

import com.google.inject.ImplementedBy;

/**
 * An in memory script repository. Should be overridden on Appengine
 * to use the distributed datastore.
 *
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
@ImplementedBy(InMemoryScriptLoader.class)
public interface ScriptLoader {
  ExecutableScript load(ScriptDescriptor name);

  ExecutableScript load(String name);
}
