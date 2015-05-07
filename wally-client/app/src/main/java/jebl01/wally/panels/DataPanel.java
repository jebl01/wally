package jebl01.wally.panels;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Size;

import com.atlassian.fugue.Option;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.DataProvider;
import jebl01.wally.RedrawListener;
import jebl01.wally.panels.layout.Layout;
import jebl01.wally.signals.Signal;

public abstract class DataPanel extends DoubleBufferedPanel implements Runnable {
    private static final int MARGIN = 5;
    private final ScheduledThreadPoolExecutor executor;
    protected final List<DataProvider> dataProviders;
    protected final Signal signal;

    public DataPanel(RedrawListener redrawListener, List<DataProvider> dataProviders, Signal signal, ScheduledThreadPoolExecutor executor, boolean showValue) {
        super(redrawListener, Layout.OVERLAY);
        this.dataProviders = dataProviders;
        this.signal = signal;
        this.executor = executor;

        if(showValue) {
            TextPanel textPanel = new TextPanel(null, Paint.Align.CENTER, false) {
                @Override
                public int textColor() {
                    return DataPanel.this.textColor();
                }

                @Override
                public int margin() {
                    Size parentSize = DataPanel.this.size;
                    return Math.min(parentSize.getHeight(), parentSize.getWidth()) / 4;
                }

                @Override
                public String getText() {
                    return String.valueOf(DataPanel.this.dataProviders.get(0).getHead());
                }

                @Override
                public int getAlpha() {
                    return 200;
                }
            };

            addChild(textPanel);
        }
    }

    @Override
    public void start() {
        //let the first dataprovider drive the drawing
        DataProvider scheduledDataProvider = dataProviders.get(0);
        this.executor.scheduleAtFixedRate(this, scheduledDataProvider.frequency.interval, scheduledDataProvider.frequency.interval, scheduledDataProvider.frequency.timeUnit);
    }

    @Override
    public void run() {
        invalidate();
    }

    @Override
    public int backgroundColor() {
        return signal.getLevel(dataProviders.get(0).getHead()).getBackgroundColor();
    }

    @Override
    public int textColor() {
        return signal.getLevel(dataProviders.get(0).getHead()).getTextColor();
    }

    @Override
    public int drawColor() {
        return signal.getLevel(dataProviders.get(0).getHead()).getDrawColor();
    }

    @Override
    public int margin() {
        return MARGIN;
    }
}
