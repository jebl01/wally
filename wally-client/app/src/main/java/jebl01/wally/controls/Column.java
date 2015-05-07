package jebl01.wally.controls;


import com.atlassian.fugue.Option;

import jebl01.wally.controls.layout.VerticalLayout;

public class Column extends Panel {
	
	public Column(EventListener eventListener, Option<String> label) {
		super(new VerticalLayout(), eventListener, label);
	}
}
