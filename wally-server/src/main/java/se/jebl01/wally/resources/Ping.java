package se.jebl01.wally.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.yammer.metrics.annotation.Timed;

@Path("/ping")
@Produces("text/plain")
public class Ping {
  private final String pong;

  public Ping(String pong) {
    this.pong = pong;
  }
  
  @GET
  @Timed
  public String ping() {
    return this.pong;
  }
}
