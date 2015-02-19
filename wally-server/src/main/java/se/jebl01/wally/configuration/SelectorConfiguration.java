package se.jebl01.wally.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SelectorConfiguration {
  @JsonProperty
  private String name;
  @JsonProperty
  private String path;
  @JsonProperty
  private String calculation;
  
  public String getName() {
    return name;
  }
  public String getPath() {
    return path;
  }
  public String getCalculation() {
    return calculation;
  }
}
