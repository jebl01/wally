package se.jebl01.wally.collectors.selectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.fugue.Option;

public abstract class Selector<T> {
  private static final Logger logger = LoggerFactory.getLogger(Selector.class);

  protected final String name;
  private final Calculator calculator;
  private static final double DEFAULT_VALUE = 0;
  
  public Selector(String name, String calculation) {
    this.name = name;
    this.calculator = Calculator.byName(calculation);
  }

  public String getName() {
    return name;
  }

  public Calculator getCalculator() {
    return calculator;
  }

  public Double getValue(T object) {
    try {
      return getValueInternal(object).map(number ->
        calculator.calculate(number.doubleValue())).getOrElse(DEFAULT_VALUE);
    }
    catch (Throwable ignored) {
      return DEFAULT_VALUE;
    }
  }

  protected abstract Option<Number> getValueInternal(T object);
  
  @Override
  public String toString() {
    return new StringBuilder()
      .append("type: ")
      .append(getClass().getSimpleName())
      .append(", name: ")
      .append(name)
      .append(", calculator: ")
      .append(calculator.getClass().getSimpleName()).toString();
  }
}