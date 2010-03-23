package com.wideplay.webthings.sitebricks;

/**
 * @author dhanji@google.com (Dhanji R. Prasanna)
 */
public class ScriptDescriptor {
  private final String url;
  private final String scriptName;
  private final String templateName;

  public ScriptDescriptor(String url, String scriptName, String templateName) {
    this.url = url;
    this.scriptName = scriptName;
    this.templateName = templateName;
  }

  public String getUrl() {
    return url;
  }

  public String getScriptName() {
    return scriptName;
  }

  public String getTemplateName() {
    return templateName;
  }
}
