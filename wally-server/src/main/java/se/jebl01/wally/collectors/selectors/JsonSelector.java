package se.jebl01.wally.collectors.selectors;

import net.minidev.json.JSONObject;

import com.atlassian.fugue.Option;
import com.jayway.jsonpath.JsonPath;

public class JsonSelector extends Selector<JSONObject> {
  final JsonPath path;

  public JsonSelector(String name, String path, String calculator) {
    super(name, calculator);
    this.path = JsonPath.compile(path);
  }

  @Override
  public Option<Number> getValueInternal(JSONObject object)  {
    Number value = path.read(object);
    
    return Option.option(value);
  }
}
