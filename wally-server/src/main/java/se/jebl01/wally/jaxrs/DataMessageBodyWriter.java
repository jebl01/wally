package se.jebl01.wally.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import net.minidev.json.JSONObject;
import se.jebl01.wally.collectors.DataRepository.Data;

import com.google.common.base.Charsets;

@Provider
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class DataMessageBodyWriter implements MessageBodyWriter<Stream<Data>>{
  
  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    if (Stream.class.isAssignableFrom(type) && genericType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());
      return (actualTypeArgs.length == 1 && actualTypeArgs[0].equals(Data.class));
    }
    return false;
  }

  @Override
  public long getSize(Stream<Data> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Stream<Data> dataStream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
    boolean bufferDump = isBufferDump(annotations);
    
    JSONObject jsonObject = dataStream.reduce(new JSONObject(), (object, data) -> {
      object.put(data.name, bufferDump ? data.getList() : data.getHead());
      return object;
      }, (leftObj, rightObj) -> {
        leftObj.putAll(rightObj);
        return leftObj;
      });
      
    if(jsonObject.isEmpty()) throw new WebApplicationException(Status.NOT_FOUND);
    
    OutputStreamWriter writer = new OutputStreamWriter(entityStream, Charsets.UTF_8);
    jsonObject.writeJSONString(writer);
    writer.flush();
  }

  private boolean isBufferDump(Annotation[] annotations) {
    for(Annotation annotation : annotations) {
      if(annotation.annotationType().equals(BufferDump.class)) return true;
    }
    return false;
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface BufferDump {
  }
}
