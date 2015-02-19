package se.jebl01.wally.resources;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import se.jebl01.wally.configuration.WallyConfiguration;
import se.jebl01.wally.configuration.WallyConfiguration.DashboardConfig;

@Path("/dashboards")
@Produces("application/json")
public class DashboardService {
  private final WallyConfiguration configuration;

  public DashboardService(WallyConfiguration configuration) {
    this.configuration = configuration;
  }

  @GET
  @Path("/")
  public Collection<String> getDashboards() {
    return configuration.getDashboards().stream().map(DashboardConfig::getName).collect(Collectors.toList());
  }
  
  @GET
  @Path("/{dashboard}")
  public DashboardConfig getDashboard(@PathParam("dashboard") String dashboard) {    
    return configuration.getDashboards()
        .stream().filter(config -> config.getName().equals(dashboard))
        .findFirst()
        .orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
  }
}
