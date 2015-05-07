package jebl01.wally.controls.layout;

import android.graphics.RectF;

import java.util.Iterator;

import jebl01.wally.controls.Panel;


public class VerticalLayout implements Layout{
	@Override
	public void layout(Panel parent, RectF bounds) {

		float childHeight = bounds.height() / parent.children().size();
		
		Iterator<Panel> childIterator = parent.children().iterator();
		
		for(int index = 0; childIterator.hasNext(); index++) {
			Panel child = childIterator.next();
			float top = bounds.top + (index * childHeight);
			float bottom = top + childHeight;
			
			child.setBounds(new RectF(bounds.left, top, bounds.right, bottom));
		}
	}
}
