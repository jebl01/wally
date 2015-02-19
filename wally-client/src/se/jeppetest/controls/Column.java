package se.jeppetest.controls;

import se.jeppetest.controls.layout.VerticalLayout;

public class Column extends Glyph{
	
	public Column(EventListener eventListener, String label) {
		super(new VerticalLayout(), eventListener, label);
	}
}
