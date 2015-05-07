package jebl01.wally.panels;

import jebl01.wally.RedrawListener;
import jebl01.wally.panels.layout.VerticalLayout;

public class Column extends Panel {

    public Column(RedrawListener redrawListener) {
        super(redrawListener, new VerticalLayout());
    }
}
