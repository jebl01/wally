package jebl01.wally.panels.layout;

import android.graphics.RectF;

import java.util.Iterator;

import jebl01.wally.panels.Panel;

public class HorizontalOverlayFlowLayout implements Layout {
    private static final int PADDING = 20;

    @Override
    public void layout(Panel parent, RectF bounds) {
        int totalPadding = (parent.getChildren().size() + 1) * PADDING;
        float childWidth = Math.min(bounds.height() - totalPadding, bounds.width() - totalPadding) / 4;
        childWidth = Math.min(childWidth, (bounds.width() - totalPadding) / parent.getChildren().size());

        Iterator<Panel> childIterator = parent.getChildren().iterator();

        for(float left = (bounds.left + PADDING); childIterator.hasNext(); left += (childWidth + PADDING)) {
            Panel child = childIterator.next();
            float right = left + childWidth;
            RectF childBounds = new RectF(left, bounds.top + PADDING, right, bounds.top + PADDING + childWidth);
            System.out.println("*********** layout overlay flow with bounds:" + childBounds);
            child.layout(childBounds);
        }
    }
}
