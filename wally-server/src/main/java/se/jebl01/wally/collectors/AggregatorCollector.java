package se.jebl01.wally.collectors;

import java.text.ParseException;
import java.util.stream.Stream;

import se.jebl01.wally.collectors.DataRepository.Data;
import se.jebl01.wally.collectors.selectors.AggregatorSelector;
import se.jebl01.wally.configuration.Parsers;
import se.jebl01.wally.configuration.Parsers.AggregatorPath;
import se.jebl01.wally.configuration.SelectorConfiguration;
import se.jebl01.wally.configuration.WallyConfiguration.FREQUENCE;

public class AggregatorCollector extends Collector<Stream<Data>, AggregatorSelector>{

  private final String path;

  public AggregatorCollector(String name, DataRepository repository, FREQUENCE frequence, String path) {
    super(name, repository, frequence);
    this.path = path;
  }

  @Override
  public void run() {
    selectors.forEach(selector ->
      repository.put(name + "." + selector.getName(), selector.getValue(repository.get(selector.paths)), frequence.bufferSize));
  }

  @Override
  public void addSelector(SelectorConfiguration selectorConfig) {
    try {
      String paths = selectorConfig.getPath().replaceAll(PATH_MACRO, this.path);
      AggregatorPath aggregatorPath = Parsers.parsePaths(paths);
      addSelector(new AggregatorSelector(selectorConfig.getName(), aggregatorPath, selectorConfig.getCalculation()));      
    } catch(ParseException e) {
      throw new RuntimeException("message: " + e.getMessage() + ", offset: " + e.getErrorOffset(), e);
    }
  }
}
