package jebl01.wally.controls;


import com.atlassian.fugue.Option;

import jebl01.wally.controls.layout.HorizontalLayout;

public class Row extends Panel {
	
	public Row(EventListener eventListener, Option<String> label) {
		super(new HorizontalLayout(), eventListener, label);
	}
}
