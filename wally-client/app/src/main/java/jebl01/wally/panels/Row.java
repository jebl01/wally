package jebl01.wally.panels;

import jebl01.wally.RedrawListener;
import jebl01.wally.panels.layout.HorizontalLayout;

public class Row extends Panel {
	public Row(RedrawListener redrawListener) {
		super(redrawListener, new HorizontalLayout());
	}
}
