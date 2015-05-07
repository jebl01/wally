package jebl01.wally.panels.layout;

import android.graphics.RectF;

import jebl01.wally.panels.Panel;

public class OverlayLayout implements Layout {
    @Override
    public void layout(Panel parent, RectF bounds) {
        for(Panel panel : parent.getChildren()) {
            panel.layout(bounds);
        }
    }
}
