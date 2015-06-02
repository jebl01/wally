package jebl01.wally.panels;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Size;

import java.util.ArrayList;
import java.util.List;

import jebl01.wally.RedrawListener;
import jebl01.wally.panels.layout.Layout;

public abstract class Panel {
    private final RedrawListener redrawListener;
    private final Layout layoutEngine;
    protected Rect bounds = new Rect();
    protected List<Panel> children = new ArrayList<>();
    protected Size size = new Size(0,0);
    private final Paint backgroundPaint;

    public Panel(RedrawListener redrawListener, Layout layoutEngine) {
        this.redrawListener = redrawListener;
        this.layoutEngine = layoutEngine;

        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(Color.DKGRAY);
        this.backgroundPaint.setStyle(Paint.Style.FILL);
    }

    public void layout(RectF bounds) {
        bounds.inset(margin(), margin());
        bounds.round(this.bounds);
        this.size = new Size(this.bounds.width(), this.bounds.height());

        this.layoutEngine.layout(this, bounds);
    }


    public void paintInternal(Canvas canvas) {
        if(Rect.intersects(canvas.getClipBounds(), this.bounds)) {
            canvas.save();
            canvas.translate(this.bounds.left, this.bounds.top);
            paintBackground(canvas);
            paint(canvas);
            canvas.restore();
            for(Panel child : this.children) {
                child.paintInternal(canvas);
            }
        }
    }

    public void paintBackground(Canvas canvas) {
        Rect rect = new Rect(0,0,this.size.getWidth(), this.size.getHeight());
        this.backgroundPaint.setColor(backgroundColor());
        canvas.drawRect(rect, this.backgroundPaint);
    }

    public void paint(Canvas canvas) {
    }

    public void invalidate() {
        redrawListener.redraw(this.bounds);
    }

    public Panel addChild(Panel child) {
        this.getChildren().add(child);
        return this;
    }
    public List<Panel> getChildren() {
        return  this.children;
    }

    public void start() {
        for(Panel child : children) {
            child.start();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append(getClass().getSimpleName())
                .append(" [");

        for(Panel child : getChildren()) {
            sb.append(child.toString());
        }

        return sb.append("]").toString();
    }

    public int margin() {
        return 0;
    }

    public Paint backgroundPaint() {
        return this.backgroundPaint;
    }

    public int backgroundColor() {
        return this.backgroundPaint.getColor();
    }

    public int textColor() {
        return Color.GRAY;
    }

    public int drawColor() {
        return this.backgroundPaint.getColor();
    }
}
