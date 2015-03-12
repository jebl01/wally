package se.jebl01.wally.collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.Test;

import se.jebl01.wally.collectors.factories.CollectorFactories;
import se.jebl01.wally.collectors.factories.CollectorFactory;
import se.jebl01.wally.configuration.CollectorConfiguration;
import se.jebl01.wally.configuration.SelectorConfiguration;
import se.jebl01.wally.configuration.WallyConfiguration.FREQUENCY;
import se.jebl01.wally.net.WallyHttpClient;

import com.google.common.collect.Lists;

public class CollectorsBuilderTest {
  @SuppressWarnings("rawtypes")
  @Test
  public void canBuildCollectorsFromConfig() throws Exception {
    DataRepository dataRepository = mock(DataRepository.class);
    WallyHttpClient httpClient = mock(WallyHttpClient.class);
    
    CollectorFactories.registerCollectorFactory("http/json", new CollectorFactory.JsonCollectorFactory(dataRepository, httpClient));
    
    Collection<CollectorConfiguration> collectorConfigs = Lists.newArrayList(createCollectorConfig());
    
    Stream<Collector> collectors = CollectorsBuilder.build(collectorConfigs);
    
    Iterator<Collector> collectorIterator = collectors.iterator();
    Collector c1 = collectorIterator.next();
    Collector c2 = collectorIterator.next();
    
    System.out.println(c1);
    System.out.println(c2);
    
    assertEquals("collector1", c1.name);
    assertEquals(FREQUENCY.SECOND, c1.frequency);

    assertEquals("collector2", c2.name);
    assertEquals(FREQUENCY.SECOND, c2.frequency);
  }
  
  
  private CollectorConfiguration createCollectorConfig() throws Exception {
    CollectorConfiguration collectorConfig = new CollectorConfiguration();
    setField(collectorConfig, "name", "collector1; collector2");
    setField(collectorConfig, "frequency", FREQUENCY.SECOND);
    setField(collectorConfig, "type", "http/json");
    setField(collectorConfig, "path", "http://foo.bar1; http://foo.bar2");
    setField(collectorConfig, "selectors", Lists.newArrayList(createSelectorConfig()));
    
    return collectorConfig;
  }
  
  private SelectorConfiguration createSelectorConfig() throws Exception {
    SelectorConfiguration selectorConfig = new SelectorConfiguration();
    setField(selectorConfig, "name", "test");
    setField(selectorConfig, "path", "test.path");
    setField(selectorConfig, "calculation", "DIFF");
    
    return selectorConfig;
  }
  private void setField(Object object, String name, Object value) throws Exception {
    Field field = object.getClass().getDeclaredField(name);
    field.setAccessible(true);
    
    field.set(object, value);
  }
}
