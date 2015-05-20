package jebl01.wally.panels;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.atlassian.fugue.Pair;

import jebl01.wally.RedrawListener;
import jebl01.wally.panels.layout.Layout;

public class LabelDecorator extends Panel {
    private final LabelPosition labelPosition;
    private final TextPanel label;
    private final Panel decorated;

    public LabelDecorator(RedrawListener redrawListener, String label, LabelPosition labelPosition, final Panel decorated) {
        this(redrawListener, label, labelPosition, Paint.Align.LEFT, decorated);
    }

    public LabelDecorator(RedrawListener redrawListener, String label, LabelPosition labelPosition, Paint.Align hAlign, final Panel decorated) {
        super(redrawListener, Layout.NONE);
        this.labelPosition = labelPosition;
        this.decorated = decorated;

        this.label = new TextPanel(label, hAlign){
            private Paint bgPaint = createBgPaint();

            @Override
            public Paint backgroundPaint() {
                bgPaint.setColor(decorated.backgroundColor());
                return bgPaint;
            }

            @Override
            public int textColor() {
                return decorated.textColor();
            }

            private Paint createBgPaint() {
                Paint paint = new Paint();

                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setScale(0.8f, 0.8f, 0.8f, 1f);

                paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                return paint;
            }
        };

        getChildren().add(this.label);
        getChildren().add(this.decorated);
    }

    @Override
    public void layout(RectF bounds) {
        super.layout(bounds);
//        bounds.inset(margin(), margin());

        Pair<RectF, RectF> labelAndChildrenBounds = this.labelPosition.getLayoutBounds(this.bounds);
        label.layout(labelAndChildrenBounds.left());

        labelAndChildrenBounds.right().inset(-margin(), -margin()); //compensate for default inset in layout
        decorated.layout(labelAndChildrenBounds.right());

    }

    @Override
    public int margin() {
        return decorated.margin();
    }

    @Override
    public String toString() {
        return super.toString() + ", label: " + this.label.getText();
    }

    public interface LabelPosition {
        public Pair<RectF, RectF> getLayoutBounds(Rect parentBounds);

        public static LabelPosition ABOVE = new Above();
        public static LabelPosition BELOW = new Below();

        public static class Above implements LabelPosition {
            public Pair<RectF, RectF> getLayoutBounds(Rect parentBounds) {
                final float labelHeight = parentBounds.height() / 6;
                final RectF labelBounds = new RectF(parentBounds.left, parentBounds.top, parentBounds.right, parentBounds.top + labelHeight);
                final RectF childBounds = new RectF(parentBounds.left, labelBounds.bottom, parentBounds.right, parentBounds.bottom);

                return Pair.pair(labelBounds, childBounds);
            }
        }

        public static class Below implements LabelPosition {
            public Pair<RectF, RectF> getLayoutBounds(Rect parentBounds) {
                final float childHeight = (parentBounds.height() / 6) * 5;
                final RectF childBounds = new RectF(parentBounds.left, parentBounds.top, parentBounds.right, parentBounds.top + childHeight);
                final RectF labelBounds = new RectF(parentBounds.left, childBounds.bottom, parentBounds.right, parentBounds.bottom);

                return Pair.pair(labelBounds, childBounds);
            }
        }
    }
}
