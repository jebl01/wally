package jebl01.wally.panels;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import jebl01.wally.RedrawListener;
import jebl01.wally.panels.layout.Layout;

public class DoubleBufferedPanel extends Panel {
    private Bitmap bitmap = null;
    private Rect bitmapRect = new Rect();
    private final Object drawLock = new Object();

    public DoubleBufferedPanel(RedrawListener redrawListener, Layout layoutEngine) {
        super(redrawListener, layoutEngine);
    }

    @Override
    public void layout(RectF bounds) {
        super.layout(bounds);

        synchronized (drawLock) {
            this.bitmap = Bitmap.createBitmap(this.size.getWidth(), this.size.getHeight(), Bitmap.Config.ARGB_8888);
            this.bitmapRect = new Rect(0,0,this.size.getWidth(), this.size.getHeight());
        }
    }

    @Override
    public void invalidate() {
        synchronized (drawLock) {
            Canvas canvas = new Canvas(this.bitmap);
            paintBackground(canvas);
            paint(canvas);
        }

        super.invalidate();
    }

    @Override
    public void paintInternal(Canvas canvas) {
        if(Rect.intersects(canvas.getClipBounds(), this.bounds)) {
            synchronized (drawLock) {
                //TODO: optimize by not always draw full bitmap
                canvas.drawBitmap(bitmap, this.bitmapRect, this.bounds, null);
            }

            for(Panel child : this.children) {
                child.paintInternal(canvas);
            }
        }
    }
}
