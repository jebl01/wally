package se.jeppetest.controls.layout;

import java.util.Iterator;

import android.graphics.RectF;
import se.jeppetest.controls.Glyph;

public class VerticalLayout implements Layout{
	@Override
	public void layout(Glyph parent, RectF bounds) {

		float childHeight = bounds.height() / parent.children().size();
		
		Iterator<Glyph> childIterator = parent.children().iterator();
		
		for(int index = 0; childIterator.hasNext(); index++) {
			Glyph child = childIterator.next();
			float top = bounds.top + (index * childHeight);
			float bottom = top + childHeight;
			
			child.setBounds(new RectF(bounds.left, top, bounds.right, bottom));
		}
	}
}
