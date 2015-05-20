package jebl01.wally.panels.contentPanels;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.atlassian.fugue.Option;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.DataProvider;
import jebl01.wally.RedrawListener;
import jebl01.wally.panels.DataPanel;
import jebl01.wally.signals.LEVEL;
import jebl01.wally.signals.Signal;

public class SideBySideGraph extends DataPanel {
    private final static float SPLITTER_WIDTH = 5;
    private final static float GRAPH_WIDTH = 5;

    private final Paint graphPaint;
    private final Paint splitterPaint;
    private final Option<Integer> yScale;
    private final Matrix matrix = new Matrix();
    private final Path path = new Path();

    public SideBySideGraph(RedrawListener redrawListener, List<DataProvider> dataProviders, Signal signal, ScheduledThreadPoolExecutor executor, Option<Integer> yScale) {
        super(redrawListener, dataProviders, signal, executor, true);
        this.yScale = yScale;

        this.graphPaint = new Paint();
        this.graphPaint.setColor(LEVEL.UNKNOWN.getDrawColor());
        this.graphPaint.setStrokeWidth(GRAPH_WIDTH);
        this.graphPaint.setStyle(Paint.Style.STROKE);
        this.graphPaint.setPathEffect(new CornerPathEffect(5f));
        this.graphPaint.setAntiAlias(true);

        this.splitterPaint = new Paint();
        this.splitterPaint.setColor(Color.BLUE);
        this.splitterPaint.setStrokeWidth(SPLITTER_WIDTH);
    }

    @Override
    public void layout(RectF bounds) {
        super.layout(bounds);

        //configure graph matrix
        this.matrix.setScale(1, -1); //flip
        this.matrix.postTranslate(0, size.getHeight());
        if(this.yScale.isDefined()) {
            this.matrix.postScale(1, this.size.getHeight() / (float) yScale.get(), 0, this.size.getHeight()); // make it fit the bounds
        }
    }

    @Override
    public void paint(Canvas canvas) {
        this.graphPaint.setColor(drawColor());
        float totalSplitterWidth = (dataProviders.size() - 1) * SPLITTER_WIDTH;
        float graphWidth = (size.getWidth() - totalSplitterWidth) / dataProviders.size();
        float x = 0;

        for(int providerIndex = dataProviders.size() - 1; providerIndex >= 0; providerIndex--) {
            final DataProvider dataProvider = dataProviders.get(providerIndex);
            final int[] data = dataProvider.getList();
            final float stepLength = graphWidth / (float)(data.length - 1);

            path.reset();

            for (int i = 0; i < data.length; i++) {

                if (i == 0 || data[i] == -1) {
                    path.moveTo(x, data[i]);
                } else {
                    path.lineTo(x, data[i]);
                }

                if ((i + 1) < data.length) {
                    x += stepLength;
                }
            }

            path.transform(this.matrix);
            canvas.drawPath(path, graphPaint);

            if (providerIndex > 0) {
                x += SPLITTER_WIDTH / 2;
                canvas.drawLine(x, 0, x, this.size.getHeight(), splitterPaint);
                x += SPLITTER_WIDTH / 2;
            }
        }

        super.paint(canvas);
    }
}
