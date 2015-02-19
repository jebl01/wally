package se.jebl01.wally.configuration;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Iterator;

import org.junit.Test;

import se.jebl01.wally.configuration.Parsers.AggregatorPath;
import se.jebl01.wally.configuration.WallyConfiguration.AGGREGATOR;
import se.jebl01.wally.configuration.WallyConfiguration.SCOPE;

public class ParsersTest {

  @Test
  public void testParsePaths() throws ParseException {
    AggregatorPath paths = Parsers.parsePaths("SUM/HEAD(dogfight1.bid, dogfight2.bid)");
    
    assertEquals(AGGREGATOR.SUM, paths.getAggregator());
    assertEquals(SCOPE.HEAD, paths.getScope());
    
    assertEquals(2, paths.getPaths().size());
    Iterator<String> pathsIterator = paths.getPaths().iterator();
    
    assertEquals("dogfight1.bid", pathsIterator.next());
    assertEquals("dogfight2.bid", pathsIterator.next());
  }

  @Test(expected=ParseException.class)
  public void testParsePathsWithErrors() throws ParseException {
    Parsers.parsePaths("SUM/HEAD(dogfight1?.bid, dogfight2.bid)");
  }
  
  @Test(expected=ParseException.class)
  public void testParsePathsInvalidAggregator() throws ParseException {
    Parsers.parsePaths("SUM(dogfight1.bid, dogfight2.bid)");
  }
}
