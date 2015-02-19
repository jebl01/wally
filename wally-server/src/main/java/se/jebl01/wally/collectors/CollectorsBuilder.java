package se.jebl01.wally.collectors;

import static se.jebl01.wally.utils.OptionUtils.flatMap;

import java.util.Collection;
import java.util.stream.Stream;

import se.jebl01.wally.collectors.factories.CollectorFactories;
import se.jebl01.wally.configuration.CollectorConfiguration;

public class CollectorsBuilder {
  @SuppressWarnings("rawtypes")
  public static Stream<Collector> build(final Collection<CollectorConfiguration> collectorConfigs) {
    return collectorConfigs.stream().flatMap(collectorConfig ->
      flatMap(CollectorFactories.getCollectorFactory(collectorConfig.getType()), collectorFactory ->
        collectorFactory.createCollectors(collectorConfig)));
  }
}
