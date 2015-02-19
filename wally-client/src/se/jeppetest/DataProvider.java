package se.jeppetest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DataProvider {
	public final long frequency;
	public final TimeUnit frequencyTimeUnit;
	public final int bufferSize;
	public final int ymax;
	public final String key;
	public AtomicInteger currentValue = new AtomicInteger(-1);
	private DataProviderListener listener;
	
	public DataProvider(String key, int bufferSize, long frequency, TimeUnit frequencyTimeUnit, int ymax) {
		this.key = key;
		this.bufferSize = bufferSize;
		this.frequency = frequency;
		this.frequencyTimeUnit = frequencyTimeUnit;
		this.ymax = ymax;
	}
	
	public void setListener(DataProviderListener listener) {
		this.listener = listener;
	}
	public int getCurrentValue() {
//		return (int)(Math.random() * 1000);
		return currentValue.get();
	}
	
	public void setValue(int value) {
		currentValue.getAndSet(value);
	}
	
	public void onBufferInit(int[] buffer) {
		if(this.listener != null) {
			this.listener.onBufferInit(buffer);
		}
	}
	
	public static interface DataProviderListener {
		void onBufferInit(int[] buffer);
	}
}
