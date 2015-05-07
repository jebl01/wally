package jebl01.wally.panels.layout;

import android.graphics.RectF;

import java.util.Iterator;

import jebl01.wally.panels.Panel;


public class HorizontalLayout implements Layout {
	@Override
	public void layout(Panel parent, RectF bounds) {

		float childWidth = bounds.width() / parent.getChildren().size();
		
		Iterator<Panel> childIterator = parent.getChildren().iterator();
		
		for(int index = 0; childIterator.hasNext(); index++) {
			Panel child = childIterator.next();
			float left = bounds.left + (index * childWidth);
			float right = left + childWidth;
			
			child.layout(new RectF(left, bounds.top, right, bounds.bottom));
		}
	}
}
