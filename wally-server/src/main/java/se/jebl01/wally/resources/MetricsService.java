package se.jebl01.wally.resources;

import java.util.Collection;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import se.jebl01.wally.collectors.DataRepository;
import se.jebl01.wally.collectors.DataRepository.Data;
import se.jebl01.wally.jaxrs.DataMessageBodyWriter.BufferDump;

@Path("/metrics")
@Produces("application/json")
public class MetricsService {
  private final DataRepository dataRepository;
  
  public MetricsService(DataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }
  
  @GET
  @Path("/{metric}")
  public Stream<Data> getMetrics(@PathParam("metric") String metric) {
    return dataRepository.get(metric);
  }
  
  @POST
  @Path("/")
  @Consumes("application/json")
  public Stream<Data> getMetrics(Collection<String> metrics) {
    return dataRepository.get(metrics);
  }
  
  @BufferDump
  @GET
  @Path("/buffers/{metric}")
  @Consumes("application/json")
  public Stream<Data> getMetricsBuffer(@PathParam("metric") String metric) {
    return dataRepository.get(metric);
  }
  
  @BufferDump
  @POST
  @Path("/buffers")
  @Consumes("application/json")
  public Stream<Data> getMetricsBuffers(Collection<String> metrics) {
    return dataRepository.get(metrics);
  }
}
