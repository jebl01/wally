package jebl01.wally.controls;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

import com.atlassian.fugue.Option;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.DataProvider;
import jebl01.wally.controls.layout.Layout;


public class Graph extends Panel implements Runnable, DataProvider.DataProviderListener {
	private Paint fgPaint = new Paint();
	private Paint textPaint = new Paint();
	
	private final int[] data;
	private volatile int pos = -1;
	private final DataProvider dataProvider;
	private final Object lock = new Object();
	private final Matrix matrix = new Matrix();
	private final Path path = new Path();
	private final Text valueText;
	private final ScheduledThreadPoolExecutor executor;

    private int yscale = 100; //hack

	public Graph(EventListener eventListener, List<DataProvider> dataProviders, ScheduledThreadPoolExecutor executor, Option<String> label) {
		super(Layout.NONE, eventListener, label);
		this.dataProvider = dataProviders.get(0); //hack to make it build...
		this.executor = executor;
		
		this.data = new int[dataProvider.frequency.bufferSize];
		this.valueText = new Text("", Align.CENTER);
		
		//Arrays.fill(data, -1);

		fgPaint.setColor(Color.GREEN);
		fgPaint.setStrokeWidth(5);
		fgPaint.setStyle(Style.STROKE);
		fgPaint.setPathEffect(new CornerPathEffect(5f));
		fgPaint.setAntiAlias(true);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(30f);

        //for(DataProvider dataProvider : this.dataProviders) {
        //    dataProvider.setListener(this);
        //}
	}

    @Override
	public void start() {
		this.executor.scheduleAtFixedRate(this, dataProvider.frequency.interval, dataProvider.frequency.interval, dataProvider.frequency.timeUnit);
	}
	
	@Override
	public void setBounds(RectF bounds) {		
		//Padding
		bounds.inset(5, 5);
		
		super.setBounds(bounds);
		
		//layout text
		RectF textBounds = new RectF(this.size);
		textBounds.inset(0, this.size.width() / 4);
		this.valueText.setBounds(textBounds);
		
		//configure graph matrix
		this.matrix.setScale(1, -1); //flip
		this.matrix.postTranslate(0, size.bottom);
		
		if(yscale > 0) {
			this.matrix.postScale(1, this.bounds.height() / yscale, 0, size.bottom); // make it fit the bounds
		}
	}

	@Override
	protected void drawCached(Canvas canvas) {
		super.drawCached(canvas);
		
		float stepLength = bounds.width() / (data.length - 1);
		float x = 0;
		path.reset();
		
		for (int i = 1; i < data.length + 1; i++) {

			int value = data[(pos + i) % data.length];

			if (i == 1 || value == -1) {
				path.moveTo(x, value);
			} else {
				path.lineTo(x, value);
			}

			x += stepLength;
		}

		path.transform(this.matrix);
		
		this.valueText.draw(canvas);
		canvas.drawPath(path, fgPaint);
	}
	
	@Override
	public void run() {
		synchronized (lock) {
			setValue(this.dataProvider.getHead());
			
			if(getBitmap() == null) return; //not fully initialized yet, eject before drawing/invalidating
			
			refreshDrawCache();
		}
		invalidate(bounds);
	}
	
	private void setValue(int value) {
		synchronized (lock) {
			this.pos++;
			if(pos == this.data.length) this.pos = 0;
			
			this.data[pos] = value;
			this.valueText.setText(Integer.toString(value));
		}
	}
	
	@Override
	public void onBufferInit(DataProvider provider) {
		synchronized (lock) {
			for(int i = 0; i < provider.frequency.bufferSize; i++) {
				setValue(provider.getList()[i]);
			}			
		}
	}
}
