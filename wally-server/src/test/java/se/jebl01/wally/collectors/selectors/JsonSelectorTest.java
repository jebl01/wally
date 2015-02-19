package se.jebl01.wally.collectors.selectors;

import static org.junit.Assert.assertEquals;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.junit.Test;

import se.jebl01.wally.collectors.TestFileLoader;

public class JsonSelectorTest {
  
  @Test
  public void canSelectFromJsonUsingDiffCalculator() {
    String name = "apa";
    String jsonPath = "$.key2.subkey1";
    String calculation = "DIFF";
    String json = TestFileLoader.loadTestFile("response.json");
    
    JSONObject jsonObject = (JSONObject)JSONValue.parse(json);
    
    JsonSelector selector = new JsonSelector(name, jsonPath, calculation);
    
    assertEquals(2d, selector.getValue(jsonObject), 0d);
    assertEquals(0d, selector.getValue(jsonObject), 0d);
  }
  
  @Test
  public void canSelectFromJsonUsingValueCalculator() {
    String name = "apa";
    String jsonPath = "$.key2.subkey1";
    String calculation = "VALUE";
    String json = TestFileLoader.loadTestFile("response.json");
    
    JSONObject jsonObject = (JSONObject)JSONValue.parse(json);
    
    JsonSelector selector = new JsonSelector(name, jsonPath, calculation);
    
    assertEquals(2d, selector.getValue(jsonObject), 0d);
    assertEquals(2d, selector.getValue(jsonObject), 0d);
  }
}
