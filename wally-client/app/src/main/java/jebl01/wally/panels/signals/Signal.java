package jebl01.wally.panels.signals;

import com.atlassian.fugue.Option;

import java.util.ArrayList;
import java.util.List;

public class Signal {
    private Option<Signal> successor = Option.none();

    private final LEVEL level;
    private final long value;
    private final COMPARATOR comparator;

    public static final Signal OK = new Signal(LEVEL.OK, 0, COMPARATOR.TRUE);
    public static final Signal UNKNOWN = new Signal(LEVEL.UNKNOWN, 0, COMPARATOR.LT);

    public Signal(LEVEL level, long value, COMPARATOR comparator) {
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

    public static class SignalBuilder {
        List<Signal> signals = new ArrayList<>();

        private SignalBuilder(Signal root) {
            signals.add(root);
        }

        public static SignalBuilder with(Signal root) {
            return new SignalBuilder(root);
        }

        public SignalBuilder then(Signal signal) {
            signals.add(signal);
            return this;
        }

        public Signal build() {
            Signal currentSignal = null;
            Signal rootSignal = null;

            for(Signal signal : signals) {
                if(rootSignal == null) {
                    rootSignal = signal;
                    currentSignal = rootSignal;
                } else {
                    currentSignal.setSuccessor(signal);
                    currentSignal = signal;
                }
            }

            return rootSignal;
        }
    }
}
