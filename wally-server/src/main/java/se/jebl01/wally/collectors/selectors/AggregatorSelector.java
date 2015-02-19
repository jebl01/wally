package se.jebl01.wally.collectors.selectors;

import java.util.Collection;
import java.util.stream.Stream;

import se.jebl01.wally.collectors.DataRepository.Data;
import se.jebl01.wally.configuration.Parsers.AggregatorPath;
import se.jebl01.wally.configuration.WallyConfiguration.AGGREGATOR;
import se.jebl01.wally.configuration.WallyConfiguration.SCOPE;

import com.atlassian.fugue.Option;

public class AggregatorSelector extends Selector<Stream<Data>>{
  private final SCOPE scope;
  private final AGGREGATOR aggregator;
  public final Collection<String> paths;

  public AggregatorSelector(String name, AggregatorPath aggregatorPath, String calculation) {
    super(name, calculation);
    this.scope = aggregatorPath.getScope();
    this.aggregator = aggregatorPath.getAggregator();
    this.paths = aggregatorPath.getPaths();
  }

  @Override
  protected Option<Number> getValueInternal(Stream<Data> data) {
    return Option.some(this.aggregator.function.apply(data, this.scope));
  }
}
