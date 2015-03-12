package se.jebl01.wally.collectors.factories;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.parboiled.common.StringUtils;

import se.jebl01.wally.collectors.AggregatorCollector;
import se.jebl01.wally.collectors.Collector;
import se.jebl01.wally.collectors.DataRepository;
import se.jebl01.wally.collectors.JsonNetworkCollector;
import se.jebl01.wally.configuration.CollectorConfiguration;
import se.jebl01.wally.net.WallyHttpClient;

import com.atlassian.fugue.Iterables;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

@SuppressWarnings("rawtypes")
public abstract class CollectorFactory {
  protected final DataRepository repository;
  private static final Splitter SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();

  public CollectorFactory(DataRepository repository) {
    this.repository = repository;
  }
  
  public abstract Stream<Collector> createCollectors(CollectorConfiguration collectorConfig);
  
  
  public static abstract class NetworkCollectorFactory extends CollectorFactory {
    protected final WallyHttpClient client;

    public NetworkCollectorFactory(DataRepository repository, WallyHttpClient client) {
      super(repository);
      this.client = client;
    }
  }
  
  public static class JsonCollectorFactory extends NetworkCollectorFactory {

    public JsonCollectorFactory(DataRepository repository, WallyHttpClient client) {
      super(repository, client);
    }

    @Override
    public Stream<Collector> createCollectors(CollectorConfiguration collectorConfig) {
      final Iterable<String> names = SPLITTER.split(collectorConfig.getName());
      final Iterable<String> paths = SPLITTER.split(collectorConfig.getPath());
      
      return StreamSupport.stream(Iterables.zip(names, paths).spliterator(), false).map(nameAndPath -> {
         Collector collector = new JsonNetworkCollector(nameAndPath.left(), repository, collectorConfig.getFrequency(), nameAndPath.right(), client);
         
         collectorConfig.getSelectors().stream().forEach(selectorConfig -> collector.addSelector(selectorConfig));
         
         return collector;
      });
    }
  }
  
  public static class AggregatingCollectorFactory extends CollectorFactory {

    public AggregatingCollectorFactory(DataRepository repository) {
      super(repository);
    }

    @Override
    public Stream<Collector> createCollectors(CollectorConfiguration collectorConfig) {
      final Iterable<String> names = SPLITTER.split(collectorConfig.getName());
      final Iterable<String> paths = SPLITTER.split(Strings.isNullOrEmpty(collectorConfig.getPath()) ? "EMPTY" : collectorConfig.getPath());
      
      return StreamSupport.stream(Iterables.zip(names, paths).spliterator(), false).map(nameAndPath -> {
         Collector collector = new AggregatorCollector(nameAndPath.left(), repository, collectorConfig.getFrequency(), nameAndPath.right());
         collectorConfig.getSelectors().stream().forEach(selectorConfig -> collector.addSelector(selectorConfig));
         
         return collector;
      });
    }
  }
}
