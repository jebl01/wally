package se.jeppetest.controls;

import se.jeppetest.controls.layout.HorizontalLayout;

public class Row extends Glyph{
	
	public Row(EventListener eventListener, String label) {
		super(new HorizontalLayout(), eventListener, label);
	}
}
