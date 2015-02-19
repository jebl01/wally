package se.jebl01.wally.collectors;

import java.util.ArrayList;
import java.util.Collection;

import se.jebl01.wally.collectors.selectors.Selector;
import se.jebl01.wally.configuration.SelectorConfiguration;
import se.jebl01.wally.configuration.WallyConfiguration.FREQUENCE;

public abstract class Collector<S, T extends Selector<S>> implements Runnable {
  public static final String PATH_MACRO = "\\$\\{path\\}";
  
  protected final DataRepository repository;
  protected final Collection<T> selectors;
  protected final String name;
  protected final FREQUENCE frequence;

  public Collector(String name, DataRepository repository, FREQUENCE frequence) {
    this.name = name;
    this.repository = repository;
    this.frequence = frequence;
    this.selectors = new ArrayList<>();
  }

  public abstract void addSelector(SelectorConfiguration selectorConfig);

  public void addSelector(T selector) {
    this.selectors.add(selector);
  }

  public String getName() {
    return name;
  }

  public FREQUENCE getFrequence() {
    return frequence;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder()
      .append("type: ")
      .append(getClass().getSimpleName())
      .append(", name: ")
      .append(name)
      .append(", buffer_type: ")
      .append(frequence)
      .append("\n");
      
    selectors.stream().forEach(selector -> sb.append("\t" + selector));
    return sb.toString();
  }
}
