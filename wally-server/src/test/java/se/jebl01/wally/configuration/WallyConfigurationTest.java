package se.jebl01.wally.configuration;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import se.jebl01.wally.collectors.TestFileLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class WallyConfigurationTest {
  
  @Test
  public void canParseConfiguration() throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    
    String textConfig = TestFileLoader.loadTestFile(WallyConfigurationTest.class, "wally-test.yaml");
    System.out.println(textConfig);
    
    WallyConfiguration config = mapper.readValue(textConfig, WallyConfiguration.class);
    
    assertNotNull(config);
  }
}
