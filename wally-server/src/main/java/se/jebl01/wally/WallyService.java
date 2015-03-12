package se.jebl01.wally;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import se.jebl01.wally.collectors.Collector;
import se.jebl01.wally.collectors.CollectorsBuilder;
import se.jebl01.wally.collectors.DataRepository;
import se.jebl01.wally.collectors.factories.CollectorFactories;
import se.jebl01.wally.collectors.factories.CollectorFactory;
import se.jebl01.wally.configuration.WallyConfiguration;
import se.jebl01.wally.configuration.WallyConfiguration.CompositePanel;
import se.jebl01.wally.configuration.WallyConfiguration.DataPanel;
import se.jebl01.wally.configuration.WallyConfiguration.Panel;
import se.jebl01.wally.jaxrs.DataMessageBodyWriter;
import se.jebl01.wally.net.HttpUtil;
import se.jebl01.wally.net.WallyHttpClient;
import se.jebl01.wally.resources.DashboardService;
import se.jebl01.wally.resources.MetricsService;

import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.google.common.base.Splitter;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

@SuppressWarnings("rawtypes")
public class WallyService extends Service<WallyConfiguration>{
  private static final Splitter SPLITTER = Splitter.on(".").omitEmptyStrings().trimResults().limit(2);
  
  public static void main(String[] args) throws Exception {
    new WallyService().run(args);
  }
  
  @Override
  public void initialize(Bootstrap<WallyConfiguration> bootstrap) {
    bootstrap.setName("wally");
  }

  @Override
  public void run(WallyConfiguration configuration, Environment environment) throws Exception {
    DataRepository dataRepository = new DataRepository();
    WallyHttpClient httpClient = new WallyHttpClient(HttpUtil.getHttpClient());
    
    CollectorFactories.registerCollectorFactory("http/json", new CollectorFactory.JsonCollectorFactory(dataRepository, httpClient));
    CollectorFactories.registerCollectorFactory("aggregate", new CollectorFactory.AggregatingCollectorFactory(dataRepository));

    Map<String, Collector> collectors = setupCollectors(configuration, environment);
    
    augmentDashboardConfig(configuration, collectors);
    
    environment.addResource(new MetricsService(dataRepository));
    environment.addResource(new DashboardService(configuration));
    environment.addResource(new DataMessageBodyWriter());
  }
  
  
  private static Pair<String, Collector> getCollector(String data, Map<String, Collector> collectors) {
    return Option.option(collectors.get(SPLITTER.split(data).iterator().next())).map(collector ->
      Pair.pair(data, collector)).getOrError(() -> "invalid data name in panel: " + data);
  }
  
  private void augmentDashboardConfig(WallyConfiguration configuration, Map<String, Collector> collectors) {
    configuration.getDashboards().stream().flatMap(config ->
      getDataPanels(config.getRootpanel())).forEach(dataPanel ->
        dataPanel.getData().keySet().stream().map(data ->
          WallyService.getCollector(data, collectors)).forEach(dataAndCollector ->
            dataPanel.setFrequency(dataAndCollector.left(), dataAndCollector.right().getFrequency())));
  }
  
  private Stream<DataPanel> getDataPanels(Panel panel) {
    if(panel instanceof DataPanel) return Stream.of((DataPanel)panel);
    
    if(panel instanceof CompositePanel) {
      CompositePanel compositePanel = (CompositePanel)panel;
      return compositePanel.getPanels().stream().flatMap(p -> getDataPanels(p));
    }
    
    return Stream.empty();
  }

  private Map<String, Collector> setupCollectors(final WallyConfiguration configuration, Environment environment) {
    final ScheduledExecutorService executor = environment.managedScheduledExecutorService("data collector %s", 30);
    
    final Map<String, Collector> collectors = new HashMap<>();
    
    CollectorsBuilder.build(configuration.getCollectors()).forEach(collector -> {
      collectors.put(collector.getName(), collector);
      executor.scheduleAtFixedRate(collector, 0, collector.getFrequency().interval, collector.getFrequency().timeUnit);
    });
    
    return collectors;
  }
}
