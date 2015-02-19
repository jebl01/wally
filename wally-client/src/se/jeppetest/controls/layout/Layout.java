package se.jeppetest.controls.layout;

import android.graphics.RectF;
import se.jeppetest.controls.Glyph;

public interface Layout {
	public void layout(Glyph parent, RectF bounds);
	
	public static Layout NONE = new NopLayout();
	
	public static class NopLayout implements Layout {
		@Override
		public void layout(Glyph parent, RectF bounds) {
		}
	}
}
