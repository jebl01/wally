package se.jebl01.wally.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.atlassian.fugue.Option;

public class WallyHttpClientTest {
  @Test
  public void successfulRequestWillReturnSomeData() throws Exception {
    HttpClient client = mock(HttpClient.class);
    when(client.executeMethod(any(WallyGetMethod.class))).thenAnswer(new Answer<Integer>() {
      public Integer answer(InvocationOnMock invocation) throws Throwable {
        HttpMethodBase method = (HttpMethodBase)invocation.getArguments()[0];
        Field responseStreamField = HttpMethodBase.class.getDeclaredField("responseStream");
        responseStreamField.setAccessible(true);
        responseStreamField.set(method, new ByteArrayInputStream("test response".getBytes()));
        
        return 200;
      }
    });
    
    WallyHttpClient wallyHttpClient = new WallyHttpClient(client);
    
    Option<String> result = wallyHttpClient.get("http://test.nu");
    
    assertTrue(result.isDefined());
    assertEquals("test response", result.get());
  }
  
  @Test
  public void failedRequestWillReturnNoData() throws Exception {
    HttpClient client = mock(HttpClient.class);
    when(client.executeMethod(any(WallyGetMethod.class))).thenReturn(500);
    
    WallyHttpClient wallyHttpClient = new WallyHttpClient(client);
    
    Option<String> result = wallyHttpClient.get("http://test.nu");
    
    assertTrue(result.isEmpty());
  }
  
  @Test
  public void catastrophicFailureWillYieldNoData() throws Exception {
    HttpClient client = mock(HttpClient.class);
    when(client.executeMethod(any(WallyGetMethod.class))).thenThrow(new IOException("fail"));
    
    WallyHttpClient wallyHttpClient = new WallyHttpClient(client);
    
    Option<String> result = wallyHttpClient.get("http://test.nu");
    
    assertTrue(result.isEmpty());
  }
}
