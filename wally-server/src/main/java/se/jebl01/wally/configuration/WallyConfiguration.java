package se.jebl01.wally.configuration;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import se.jebl01.wally.collectors.DataRepository.Data;
import se.jebl01.wally.configuration.Parsers.SignalValue;

import com.atlassian.fugue.Function2;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.yammer.dropwizard.config.Configuration;

public class WallyConfiguration extends Configuration {
  private static final Splitter SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();

  public static enum SIGNAL_COMPARATOR {
    EQ,HT,LT
  }
  public static enum FREQUENCE {
    SECOND(1, TimeUnit.SECONDS, 60), MINUTE(1, TimeUnit.MINUTES, 60);

    public final int interval;
    public final TimeUnit timeUnit;
    public int bufferSize;

    FREQUENCE(int interval, TimeUnit timeUnit, int bufferSize) {
      this.interval = interval;
      this.timeUnit = timeUnit;
      this.bufferSize = bufferSize;
    }
  }
  public static enum SIGNAL_LEVEL {
    WARNING, CRITICAL
  }
  
  public static enum SCOPE {
    HEAD(data -> DoubleStream.builder().add(data.getHead()).build()),
    ALL(data -> DoubleStream.of(data.getList()));
    
    public final Function<Data, DoubleStream> function;

    SCOPE(Function<Data, DoubleStream> func) {
      this.function = func;
    }
  }
  
  public static enum AGGREGATOR {
    SUM((stream, scope) -> stream.mapToDouble(d -> scope.function.apply(d).sum()).sum()),
    AVG((stream, scope) -> stream.mapToDouble(d -> scope.function.apply(d).average().getAsDouble()).average().getAsDouble());
    
    public final Function2<Stream<Data>, SCOPE, Double> function;

    private AGGREGATOR(Function2<Stream<Data>, SCOPE, Double> func) {
      this.function = func;
    }
  }
  
  @JsonProperty
  private Collection<CollectorConfiguration> collectors;
  
  @JsonProperty
  private Collection<DashboardConfig> dashboards;
  
  @JsonProperty
  private Collection<Signal> signals;
  
  public Collection<CollectorConfiguration> getCollectors() {
    return collectors;
  }
  
  public Collection<DashboardConfig> getDashboards() {
    return dashboards;
  }
  
  public static class DashboardConfig {
    @JsonProperty
    private String name;
    @JsonProperty
    private Panel rootpanel;
    
    public String getName() {
      return name;
    }
    
    public Panel getRootpanel() {
      return rootpanel;
    }
  }

  @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="type")
  @JsonSubTypes({
        @JsonSubTypes.Type(value=Row.class, name="row"),
        @JsonSubTypes.Type(value=Column.class, name="column"),
        @JsonSubTypes.Type(value=Graph.class, name="graph")
    })
  @JsonInclude(Include.NON_EMPTY)
  public static class Panel {
    @JsonProperty
    private String label;
    @JsonProperty
    private String type;
    
    public String getLabel() {
      return label;
    }
    
    public String getType() {
      return type;
    }
  }
  
  public static interface CompositePanel {
    List<? extends Panel> getPanels();
  }
  
  @JsonInclude(Include.NON_EMPTY)
  public static class Row extends Panel implements CompositePanel{
    @JsonProperty
    private List<Panel> panels;
    
    @Override
    public List<Panel> getPanels() {
      return panels;
    }
  }
  
  @JsonInclude(Include.NON_EMPTY)
  public static class Column extends Panel implements CompositePanel{
    @JsonProperty
    private List<Panel> panels;
    
    @Override
    public List<Panel> getPanels() {
      return panels;
    }
  }
  
  public abstract static class DataPanel extends Panel{
    @JsonIgnore
    private Map<String, FREQUENCE> data = new HashMap<>(); 
    
    @JsonProperty
    private List<Signal> signals;
    
    public void setFrequence(String data, FREQUENCE frequence) {
      this.data.put(data, frequence);
    }
    
    @JsonProperty
    public void setData(String data) {
      SPLITTER.split(data).forEach(d -> this.data.put(d, null));
    }
    
    @JsonProperty
    public Map<String, FREQUENCE> getData() {
      return data;
    }
    
    public List<Signal> getSignals() {
      return signals;
    }
  }
  
  @JsonInclude(Include.NON_EMPTY)
  public static class Graph extends DataPanel {
       
    @JsonProperty
    private Integer yscale;
    
    public Integer getYscale() {
      return yscale;
    }
  }
  
  public static class Signal {
    private static final Map<String, Signal> signals = new HashMap<>();
    
    @JsonIgnore
    private SIGNAL_COMPARATOR comparator;
    
    @JsonIgnore
    private int value;
    
    @JsonIgnore
    private SIGNAL_LEVEL level;

    @JsonIgnore
    private String name;
    
    public Signal (
        @JsonProperty("name") String name,
        @JsonProperty("level") SIGNAL_LEVEL level,
        @JsonProperty("value") String valueString) throws ParseException {
      
      SignalValue signalValue = Parsers.parseSignalValue(valueString);
      this.comparator = signalValue.getComparator();
      this.value = signalValue.getValue();
      this.name = name;
      this.level = level;
      
      signals.put(this.name, this); //leaked this reference, but hey...
    }
    
    @JsonCreator
    public static Signal byName(String name) {
      if(!signals.containsKey(name)) throw new IllegalArgumentException("no signal with name: " + name);
      return signals.get(name);
    }
    
    @JsonProperty
    public SIGNAL_COMPARATOR getComparator() {
      return comparator;
    }
    
    @JsonProperty
    public int getValue() {
      return value;
    }
    
    @JsonProperty
    public SIGNAL_LEVEL getLevel() {
      return level;
    }
    
    @JsonProperty
    public String getName() {
      return name;
    }
    
    @Override
    public String toString() {
      return new StringBuilder()
        .append("name: ").append(this.name).append(System.lineSeparator())
        .append("comparator: ").append(this.comparator).append(System.lineSeparator())
        .append("value: ").append(this.value).append(System.lineSeparator())
        .append("level: ").append(this.level).toString();
        
    }
  }
}
