package se.jebl01.wally.net;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.fugue.Option;

public class WallyHttpClient {
  private final HttpClient httpClient;
  private static final Logger logger = LoggerFactory.getLogger(WallyHttpClient.class);
  
  public WallyHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Option<String> get(String url) {
    try (WallyGetMethod method = new WallyGetMethod(url)) {
      if (this.httpClient.executeMethod(method) == 200) {
        method.getResponseHeader("encoding");
        InputStream stream = method.getResponseBodyAsStream();
        if(stream == null) return Option.none();
        return Option.option(IOUtils.toString(stream, method.getResponseCharSet()));
      }
    } catch (Exception e) {
      logger.error("failed to get data", e);
    }
    
    return Option.none();
  }
}
