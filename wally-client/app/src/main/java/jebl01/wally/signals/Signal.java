package jebl01.wally.signals;

import com.atlassian.fugue.Option;

public class Signal {
    private Option<Signal> successor = Option.none();

    private final LEVEL level;
    private final double value;
    private final COMPARATOR comparator;

    public static final Signal OK = new Signal(LEVEL.OK, 0, COMPARATOR.TRUE);
    public static final Signal UNKNOWN = new Signal(LEVEL.UNKNOWN, 0, COMPARATOR.LT);

    public Signal(LEVEL level, double value, COMPARATOR comparator) {
        this.level = level;
        this.value = value;
        this.comparator = comparator;
    }

    public Signal setSuccessor(Signal successor) {
        this.successor = Option.some(successor);
        return successor;
    }

    public LEVEL getLevel(double value) {
        if(this.comparator.apply(this.value, value)) {
            return this.level;
        } else if(this.successor.isDefined()) {
            return this.successor.get().getLevel(value);
        } else {
            return LEVEL.UNKNOWN;
        }
    }
}
