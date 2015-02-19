package se.jebl01.wally.collectors.factories;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.fugue.Option;

public class CollectorFactories {
  private static Map<String, CollectorFactory> factories = new HashMap<>();
  
  public static void registerCollectorFactory(String type, CollectorFactory factory) {
    factories.put(type, factory);
  }
  
  public static Option<CollectorFactory> getCollectorFactory(String type) {
    return Option.option(factories.get(type));
  }
}
