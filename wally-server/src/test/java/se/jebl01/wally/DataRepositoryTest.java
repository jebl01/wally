package se.jebl01.wally;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.jebl01.wally.collectors.DataRepository;
import se.jebl01.wally.collectors.DataRepository.Data;

public class DataRepositoryTest {
  
  @Test
  public void canQueryRepositoryForData() {
    DataRepository repository = new DataRepository();
    String name = "collector1.selector1";
    int bufferSize = 10;
    
    for (int i = 0; i < 100; i++) {
      Number number = i;
      repository.put(name, number.doubleValue(), bufferSize);
    }
    
    Data data = repository.get(name).findFirst().get();
    
    assertEquals(99d, data.getHead(), 0.1);
  }
  
  @Test
  public void repositoryPutWillResultInDataPut() {
    double[] expected = {90.0,91.0,92.0,93.0,94.0,95.0,96.0,97.0,98.0,99.0};
    
    DataRepository repository = new DataRepository();
    String name = "collector1.selector1";
    int bufferSize = 10;
    
    for (int i = 0; i < 100; i++) {
      Number number = i;
      repository.put(name, number.doubleValue(), bufferSize);
    }
    
    Data data = repository.get(name).findFirst().get();
    assertArrayEquals(expected, data.getList(), 0.1);
  }
  
  @Test
  public void willGetCorrectDataNotFullBuffer() {
    Data data = new Data("test.test", 10);
    double[] expecteds = new double[]{0,0,0,0,0,0,1,2,3,4};
    
    for (int i = 0; i < 5; i++) {
      data.put(i);
    }

    assertArrayEquals(expecteds, data.getList(), 0d);
  }
}
