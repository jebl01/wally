package jebl01.wally;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DataProvider {
    private final int[] data;
    private volatile int cursor = -1;

    public final FREQUENCY frequency;
	public final String key;

	public DataProvider(String key, FREQUENCY frequency) {
		this.key = key;
        this.frequency = frequency;
        this.data = new int[frequency.bufferSize];
        Arrays.fill(this.data, -1);
	}
	
	public synchronized void setValue(int value) {
        cursor = (cursor + 1) % frequency.bufferSize;
        data[cursor] = value;
	}

    public synchronized int getHead() {
        return cursor != -1 ? data[cursor] : -1;
    }

    public synchronized int[] getList() {
        final int[] dest = new int[frequency.bufferSize];
        if(cursor == frequency.bufferSize - 1 || cursor == -1) return Arrays.copyOf(data, frequency.bufferSize);

        System.arraycopy(data, 0, dest, frequency.bufferSize - cursor - 1, cursor + 1);
        System.arraycopy(data, cursor + 1, dest, 0, frequency.bufferSize - cursor - 1);

        return dest;
    }

	public synchronized void onBufferInit(int[] buffer) {
        System.arraycopy(buffer, 0, data, 0, buffer.length);
        cursor = frequency.bufferSize - 1;
	}

    public static enum FREQUENCY {
        SECOND(1, TimeUnit.SECONDS, 60), MINUTE(1, TimeUnit.MINUTES, 60);

        public final int interval;
        public final TimeUnit timeUnit;
        public int bufferSize;

        FREQUENCY(int interval, TimeUnit timeUnit, int bufferSize) {
            this.interval = interval;
            this.timeUnit = timeUnit;
            this.bufferSize = bufferSize;
        }

        public long toMillis() {
            return timeUnit.toMillis(interval);
        }
    }
}
