package se.jebl01.wally.collectors;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

public class DataRepository {
  ConcurrentSkipListMap<String, Data> data = new ConcurrentSkipListMap<>();
  
  public void put(String name, double value, int bufferSize) {
    if(!data.containsKey(name)) {
      data.putIfAbsent(name, new Data(name, bufferSize));      
    }
    
    data.get(name).put(value);
  }
  
  public Stream<Data> get(String... names) {
    return get(Arrays.asList(names));
  }
  
  public Stream<Data> get(Collection<String> names) {
    if(names == null) return Stream.empty();
    return names.stream().flatMap(name -> data.subMap(name, true, name + "zzz", false).values().stream());
  }
  
  public static class Data {
    private final double[] data;
    public final int bufferSize;
    private volatile int cursor = -1;
    public final String name;
    
    public Data(String name, int bufferSize) {
      this.name = name;
      this.bufferSize = bufferSize;
      this.data = new double[bufferSize];
      Arrays.fill(this.data, 0);
    }
    
    public synchronized void put(double value) {
      cursor = (cursor + 1) % bufferSize;
      data[cursor] = value;
    }
    
    public synchronized double getHead() {
      return cursor != -1 ? data[cursor] : -1;
    }
    
    public synchronized double[] getList() {
      double[] dest = new double[bufferSize];
      if(cursor == bufferSize - 1 || cursor == -1) return Arrays.copyOf(data, bufferSize);
      
      System.arraycopy(data, 0, dest, bufferSize - cursor - 1, cursor + 1);
      System.arraycopy(data, cursor + 1, dest, 0, bufferSize - cursor - 1);
      
      return dest;
    }
  }
}
