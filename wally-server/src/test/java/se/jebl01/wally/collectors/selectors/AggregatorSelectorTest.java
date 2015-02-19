package se.jebl01.wally.collectors.selectors;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;

import se.jebl01.wally.collectors.DataRepository.Data;
import se.jebl01.wally.configuration.Parsers.AggregatorPath;

public class AggregatorSelectorTest {
  @Test
  public void canSelectFromAggregator() {
    Stream<Data> dataStream1 = Stream.of(getData(1, 10), getData(11, 10));
    Stream<Data> dataStream2 = Stream.of(getData(1, 10), getData(11, 10));
    Stream<Data> dataStream3 = Stream.of(getData(1, 10), getData(11, 10));
    Stream<Data> dataStream4 = Stream.of(getData(1, 10), getData(11, 10));
    
    AggregatorPath headSumming = new AggregatorPath().aggregator("SUM").scope("HEAD");
    AggregatorPath headAvg = new AggregatorPath().aggregator("AVG").scope("HEAD");
    AggregatorPath allSumming = new AggregatorPath().aggregator("SUM").scope("ALL");
    AggregatorPath allAvg = new AggregatorPath().aggregator("AVG").scope("ALL");
    
    assertEquals(30d, new AggregatorSelector("test", headSumming, "VALUE").getValue(dataStream1).doubleValue(), 0.1d);
    assertEquals(15d, new AggregatorSelector("test", headAvg, "VALUE").getValue(dataStream2).doubleValue(), 0.1d);
    assertEquals(210d, new AggregatorSelector("test", allSumming, "VALUE").getValue(dataStream3).doubleValue(), 0.1d);
    assertEquals(10.5d, new AggregatorSelector("test", allAvg, "VALUE").getValue(dataStream4).doubleValue(), 0.1d);
  }
  
  private Data getData(int start, int length) {
    Data data = new Data("test.test", length);
    for(int i = 0; i < length; i++) {
      data.put(i+start);
    }
    return data;
  }
}
