package jebl01.wally.controls.layout;

import android.graphics.RectF;

import jebl01.wally.controls.Panel;


public interface Layout {
	public void layout(Panel parent, RectF bounds);
	
	public static Layout NONE = new NopLayout();
	
	public static class NopLayout implements Layout {
		@Override
		public void layout(Panel parent, RectF bounds) {
		}
	}
}
