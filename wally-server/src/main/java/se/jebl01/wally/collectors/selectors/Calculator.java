package se.jebl01.wally.collectors.selectors;

public class Calculator {
  public static final String DIFF_CALCULATOR = "diff";
  public static final String VALUE_CALCULATOR = "value";
  
  public double calculate(double value) {return value;}

  public static Calculator byName(String calculation) {
    switch (calculation) {
    case "DIFF":
      return new DiffCalculator();
    case "VALUE":
      return new Calculator() {};
    default:
      throw new IllegalArgumentException("invalid calculation type: " + calculation);
    }
  }

  public static class DiffCalculator extends Calculator {
    private volatile double oldValue = 0;

    @Override
    public double calculate(double value) {
      try {
        return Math.abs(value - oldValue);
      } finally {
        oldValue = value;
      }
    }
  }
}
