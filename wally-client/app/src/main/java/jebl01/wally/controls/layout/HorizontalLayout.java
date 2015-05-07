package jebl01.wally.controls.layout;

import android.graphics.RectF;

import java.util.Iterator;

import jebl01.wally.controls.Panel;


public class HorizontalLayout implements Layout{
	@Override
	public void layout(Panel parent, RectF bounds) {

		float childWidth = bounds.width() / parent.children().size();
		
		Iterator<Panel> childIterator = parent.children().iterator();
		
		for(int index = 0; childIterator.hasNext(); index++) {
			Panel child = childIterator.next();
			float left = bounds.left + (index * childWidth);
			float right = left + childWidth;
			
			child.setBounds(new RectF(left, bounds.top, right, bounds.bottom));
		}
	}
}
