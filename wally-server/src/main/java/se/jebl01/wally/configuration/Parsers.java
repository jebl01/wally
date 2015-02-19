package se.jebl01.wally.configuration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import se.jebl01.wally.configuration.WallyConfiguration.AGGREGATOR;
import se.jebl01.wally.configuration.WallyConfiguration.SCOPE;
import se.jebl01.wally.configuration.WallyConfiguration.SIGNAL_COMPARATOR;

public class Parsers {  
  public static AggregatorPath parsePaths(String paths) throws ParseException {
    return parse(paths, AggregatorPathParser.class); 
  }
  
  public static SignalValue parseSignalValue(String value) throws ParseException {
    return parse(value, SignalValueParser.class); 
  }
  
  private static <T, P extends RootedBaseParser<T>> T parse(String paths, Class<P> parserClass) throws ParseException {
    P parser = Parboiled.createParser(parserClass);
    ParsingResult<T> result = new ReportingParseRunner<T>(parser.rootRule()).run(paths);
    
    if(!result.getParseErrors().isEmpty()) {
      ParseError e = result.getParseErrors().get(0);
      throw new ParseException(e.getErrorMessage(), e.getStartIndex());
    }
    return result.getTopStackValue();
  }
  
  abstract static class RootedBaseParser<T> extends BaseParser<T> {
    abstract Rule rootRule();
  }
  
  static class SignalValueParser extends RootedBaseParser<SignalValue> {

    Rule rootRule() {
      return sequence(push(new SignalValue()),
          comparator(), push(pop().comparator(match())),
          '(', oneOrMore(digit()), push(pop().value(match())), ')');
    }
    
    Rule comparator() {
      return firstOf(string("EQ"), string("LT"), string("HT"));
    }
  }
  static class AggregatorPathParser extends RootedBaseParser<AggregatorPath> {
    
    Rule rootRule() {
      return sequence(push(new AggregatorPath()),
          aggregator(), push(pop().aggregator(match())), '/', scope(), push(pop().scope(match())),
          '(', paths(), ')');
    }
    
    Rule paths() {
      return sequence(path(), push(pop().path(match())), optional(pathSeparator(), paths()));
    }
    
    Rule path() {
      return sequence(word(), optional(sequence('.', path())));
    }
    
    Rule scope() {
      return firstOf(string("ALL"), string("HEAD"));
    }
    
    Rule aggregator() {
      return firstOf(string("SUM"), string("AVG"));
    }
    
    Rule pathSeparator() {
      return sequence(zeroOrMore(' '), ',', zeroOrMore(' '));
    }
    
    Rule word() {
      return oneOrMore(firstOf(alpha(), digit(), anyOf("-_")));
    }
  }
  
  public static class SignalValue {
    
    private SIGNAL_COMPARATOR comparator;
    private int value;

    public SignalValue comparator(String comparator) {
      this.comparator = SIGNAL_COMPARATOR.valueOf(comparator);
      return this;
    }
    
    public SignalValue value(String value) {
      this.value = Integer.parseInt(value);
      return this;
    }
    
    public SIGNAL_COMPARATOR getComparator() {
      return comparator;
    }
    
    public int getValue() {
      return value;
    }
  }
  
  public static class AggregatorPath {
    private List<String> path = new ArrayList<>();
    private AGGREGATOR aggregator = null;
    private SCOPE scope = null;
    
    public AggregatorPath path(String path) {
      this.path.add(path);
      return this;
    }

    public AggregatorPath aggregator(String aggregator) {
      this.aggregator = AGGREGATOR.valueOf(aggregator);
      return this;
    }
    
    public AggregatorPath scope(String scope) {
      this.scope = SCOPE.valueOf(scope);
      return this;
    }
    
    public AGGREGATOR getAggregator() {
      return aggregator;
    }
    
    public Collection<String> getPaths() {
      return path;
    }
    
    public SCOPE getScope() {
      return scope;
    }
  }
}
