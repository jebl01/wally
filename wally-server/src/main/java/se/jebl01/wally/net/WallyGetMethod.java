package se.jebl01.wally.net;

import java.io.IOException;

import org.apache.commons.httpclient.methods.GetMethod;

public class WallyGetMethod extends GetMethod implements AutoCloseable {
  public WallyGetMethod(String url) {
    super(url);
  }
  
  @Override
  public void close() throws IOException {
    this.releaseConnection();
  }
}
