package se.jebl01.wally.collectors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.jebl01.wally.collectors.selectors.AggregatorSelector;
import se.jebl01.wally.configuration.Parsers.AggregatorPath;
import se.jebl01.wally.configuration.WallyConfiguration.FREQUENCY;

public class AggregatorCollectorTest {
  
  @Test
  public void testSumAggregatingCollector() {
    DataRepository repository = new DataRepository();
    
    repository.put("test.key1", 10, 10);
    repository.put("test.key2", 20, 10);
    repository.put("test.key3", 30, 10);
    
    AggregatorCollector collector = new AggregatorCollector("aggregated", repository, FREQUENCY.SECOND, "");
    
    AggregatorPath headSumming = new AggregatorPath()
      .aggregator("SUM")
      .scope("HEAD")
      .path("test.key1")
      .path("test.key2")
      .path("test.key3");

    AggregatorSelector selector = new AggregatorSelector("agg_key1", headSumming, "VALUE");
    collector.addSelector(selector);
    collector.run();
    
    assertEquals(60.0, repository.get("aggregated.agg_key1").findFirst().get().getHead(), 0.1d);
  }
  
  @Test
  public void testAvgAggregatingCollector() {
    DataRepository repository = new DataRepository();
    
    repository.put("test.key1", 10, 10);
    repository.put("test.key1", 10, 10);
    repository.put("test.key1", 10, 10);
    
    AggregatorCollector collector = new AggregatorCollector("aggregated", repository, FREQUENCY.SECOND, "");
    
    AggregatorPath headSumming = new AggregatorPath()
      .aggregator("AVG")
      .scope("ALL")
      .path("test.key1");

    AggregatorSelector selector = new AggregatorSelector("agg_key1", headSumming, "VALUE");
    collector.addSelector(selector);
    
    collector.run();
    
    assertEquals(3.0, repository.get("aggregated.agg_key1").findFirst().get().getHead(), 0.1d);
  }
}
