package se.jebl01.wally.collectors;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import se.jebl01.wally.collectors.selectors.JsonSelector;
import se.jebl01.wally.configuration.SelectorConfiguration;
import se.jebl01.wally.configuration.WallyConfiguration.FREQUENCY;
import se.jebl01.wally.net.WallyHttpClient;

public class JsonNetworkCollector extends NetworkCollector<JSONObject, JsonSelector> {

  public JsonNetworkCollector(String name, DataRepository repository, FREQUENCY bufferType, String path, WallyHttpClient client) {
    super(name, repository, bufferType, path, client);
  }

  @Override
  protected JSONObject parseData(String data) {
    return (JSONObject)JSONValue.parse(data);
  }

  @Override
  public void addSelector(SelectorConfiguration selectorConfig) {
    addSelector(new JsonSelector(selectorConfig.getName(), selectorConfig.getPath(), selectorConfig.getCalculation()));
  }
}
