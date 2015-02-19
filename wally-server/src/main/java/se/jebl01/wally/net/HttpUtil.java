package se.jebl01.wally.net;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpUtil {
  private static final HttpClientParams HTTP_CLIENT_PARAMS;

  private static final MultiThreadedHttpConnectionManager CONNECTION_MANAGER;

  static {
    CONNECTION_MANAGER = new MultiThreadedHttpConnectionManager();
    CONNECTION_MANAGER.getParams().setTcpNoDelay(false);
    CONNECTION_MANAGER.getParams().setStaleCheckingEnabled(true);
    CONNECTION_MANAGER.getParams().setSoTimeout(1000);
    CONNECTION_MANAGER.getParams().setConnectionTimeout(1000);
    
    HTTP_CLIENT_PARAMS = new HttpClientParams();
    HTTP_CLIENT_PARAMS.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
  }

  public static HttpClient getHttpClient() {
    HttpClient httpClient = new HttpClient(HTTP_CLIENT_PARAMS);
    httpClient.setHttpConnectionManager(CONNECTION_MANAGER);
    return httpClient;
  }
}
