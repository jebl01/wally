package se.jeppetest.controls.layout;

import java.util.Iterator;

import android.graphics.RectF;
import se.jeppetest.controls.Glyph;

public class HorizontalLayout implements Layout{
	@Override
	public void layout(Glyph parent, RectF bounds) {

		float childWidth = bounds.width() / parent.children().size();
		
		Iterator<Glyph> childIterator = parent.children().iterator();
		
		for(int index = 0; childIterator.hasNext(); index++) {
			Glyph child = childIterator.next();
			float left = bounds.left + (index * childWidth);
			float right = left + childWidth;
			
			child.setBounds(new RectF(left, bounds.top, right, bounds.bottom));
		}
	}
}
