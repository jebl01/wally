package jebl01.wally.panels.signals;

import com.atlassian.fugue.Function2;

public enum COMPARATOR {
    EQ(new Function2<Double, Double, Boolean>() {
        public Boolean apply(Double value, Double measuredValue) {
            return measuredValue.compareTo(value) == 0;
        }
    }),
    HT(new Function2<Double, Double, Boolean>() {
        public Boolean apply(Double value, Double measuredValue) {
            return measuredValue.compareTo(value) > 0;
        }
    }),
    LT(new Function2<Double, Double, Boolean>() {
        public Boolean apply(Double value, Double measuredValue) {
            return measuredValue.compareTo(value) < 0;
        }
    }),
    TRUE(new Function2<Double, Double, Boolean>() {
        public Boolean apply(Double value, Double measuredValue) {
            return true;
        }
    });

    private final Function2<Double, Double, Boolean> comparator;

    private COMPARATOR(Function2<Double, Double, Boolean> comparator) {
        this.comparator = comparator;
    }

    public boolean apply(double value, double measuredValue) {
        return this.comparator.apply(value, measuredValue);
    }
}
