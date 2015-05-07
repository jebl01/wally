package jebl01.wally.panels.contentPanels;

import jebl01.wally.RedrawListener;
import jebl01.wally.panels.Panel;
import jebl01.wally.panels.layout.Layout;

public class ColorPanel extends Panel {
    private final int color;

    public ColorPanel(RedrawListener redrawListener, int color) {
        super(redrawListener, Layout.NONE);
        this.color = color;
    }

    @Override
    public int backgroundColor() {
        return this.color;
    }

    @Override
    public int margin() {
        return 5;
    }
}
