package jebl01.wally.panels.layout;

import android.graphics.RectF;

import jebl01.wally.panels.Panel;


public interface Layout {
	public void layout(Panel parent, RectF bounds);
	
	public static Layout NONE = new NopLayout();
	public static Layout HORIZONTAL = new HorizontalLayout();
	public static Layout VERTICAL = new VerticalLayout();
    public static Layout OVERLAY = new OverlayLayout();

	public static class NopLayout implements Layout {
		@Override
		public void layout(Panel parent, RectF bounds) {
		}
	}
}
