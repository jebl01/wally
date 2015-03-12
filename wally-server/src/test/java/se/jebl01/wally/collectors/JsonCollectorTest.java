package se.jebl01.wally.collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import se.jebl01.wally.collectors.selectors.JsonSelector;
import se.jebl01.wally.configuration.WallyConfiguration.FREQUENCY;
import se.jebl01.wally.net.WallyHttpClient;

import com.atlassian.fugue.Option;

public class JsonCollectorTest {
  
  @Test
  public void testJsonCollectorCanCollectData() {
    WallyHttpClient client = mock(WallyHttpClient.class);
    String collectorName = "dogfight";
    String url = "test.url";
    
    String selectorName = "queries";
    String path = "key2.subkey1";
    String calculation = "DIFF";
    String testData = TestFileLoader.loadTestFile("response.json");
    DataRepository repository = new DataRepository();

    when(client.get(url)).thenReturn(Option.some(testData));
    
    JsonNetworkCollector collector = new JsonNetworkCollector(collectorName, repository, FREQUENCY.SECOND, url, client);
    JsonSelector selector = new JsonSelector(selectorName, path, calculation);
    collector.addSelector(selector);
    
    collector.run();
    
    assertEquals(2.0, repository.get("dogfight.queries").findFirst().get().getHead(), 0.1d);
  }
}
