package se.jebl01.wally.collectors;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class TestFileLoader {

  public static String loadTestFile(final String name) {
    return loadTestFile(TestFileLoader.class, name);
  }
  public static String loadTestFile(Class<?> clazz, final String name) {
    final String path = clazz.getPackage().getName().replace('.', '/') + '/';
    String filePath = path + name;

    final InputStream is = TestFileLoader.class.getClassLoader().getResourceAsStream(filePath);
    if(is == null)
    {
      throw new IllegalArgumentException("Unable to find template: " + filePath);
    }
    try
    {
      return IOUtils.toString(is, "UTF-8");
    }
    catch(final IOException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  public static InputStream loadTestFileAsStream(final String name) {
    return loadTestFileAsStream(TestFileLoader.class, name);
  }
  public static InputStream loadTestFileAsStream(Class<?> clazz, final String name) {
    final String path = clazz.getPackage().getName().replace('.', '/') + '/';
    String filePath = path + name;

    return TestFileLoader.class.getClassLoader().getResourceAsStream(filePath);
  }
}
