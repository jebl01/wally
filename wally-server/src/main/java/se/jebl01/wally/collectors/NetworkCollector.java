package se.jebl01.wally.collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.jebl01.wally.collectors.selectors.Selector;
import se.jebl01.wally.configuration.WallyConfiguration.FREQUENCE;
import se.jebl01.wally.net.WallyHttpClient;

import com.atlassian.fugue.Option;

public abstract class NetworkCollector<S, T extends Selector<S>> extends Collector<S, T> {
  private static final Logger logger = LoggerFactory.getLogger(NetworkCollector.class);

  private final String path;
  private final WallyHttpClient client;

  public NetworkCollector(String name, DataRepository repository, FREQUENCE bufferType, String path, WallyHttpClient client) {
    super(name, repository, bufferType);
    this.path = path;
    this.client = client;
  }

  @Override
  public void run() {
    client.get(this.path).foreach(inputStream ->
      parseDataInternal(inputStream).foreach(parsedData ->
        selectors.forEach(selector ->
          repository.put(name + "." + selector.getName(), selector.getValue(parsedData), frequence.bufferSize))));
  }

  protected abstract S parseData(String data);

  private Option<S> parseDataInternal(String data) {
    try {
      return Option.option(parseData(data));
    }
    catch (Throwable t) {
      logger.warn("failed to parse data", t);
      return Option.none();
    }
  }
  
}
