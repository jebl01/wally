package jebl01.wally.panels.layout;

import android.graphics.RectF;

import java.util.Iterator;

import jebl01.wally.panels.Panel;


public class VerticalLayout implements Layout {
	@Override
	public void layout(Panel parent, RectF bounds) {

		float childHeight = bounds.height() / parent.getChildren().size();
		
		Iterator<Panel> childIterator = parent.getChildren().iterator();
		
		for(int index = 0; childIterator.hasNext(); index++) {
			Panel child = childIterator.next();
			float top = bounds.top + (index * childHeight);
			float bottom = top + childHeight;
			
			child.layout(new RectF(bounds.left, top, bounds.right, bottom));
		}
	}
}
