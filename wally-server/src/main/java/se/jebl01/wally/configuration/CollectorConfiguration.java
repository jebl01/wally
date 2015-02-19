package se.jebl01.wally.configuration;

import java.util.Collection;

import se.jebl01.wally.configuration.WallyConfiguration.FREQUENCE;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CollectorConfiguration {
  @JsonProperty
  private String name;
  @JsonProperty
  private FREQUENCE frequence;
  @JsonProperty
  private String type;
  @JsonProperty
  private String path;
  @JsonProperty
  private Collection<SelectorConfiguration> selectors;
  
  
  public String getName() {
    return name;
  }
  public FREQUENCE getFrequence() {
    return frequence;
  }
  public String getType() {
    return type;
  }
  public String getPath() {
    return path == null ? "" : path;
  }
  public Collection<SelectorConfiguration> getSelectors() {
    return selectors;
  }
}
