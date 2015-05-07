package jebl01.wally;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import jebl01.wally.controls.EventListener;
import jebl01.wally.controls.Panel;

//import android.content.Context;

public class RootView extends View implements EventListener {

	private Panel root;

	public RootView(Context context) {
		super(context);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		System.out.println("onLayout!");
		if (root != null) {
			System.out.println("will layout!: " + changed + " " + left + " " + top + " " + right + " " + bottom);
			root.setBounds(new RectF(left, top, right, bottom));
		}
	}

	public void setRootPanel(Panel root) {
		String label = root != null ? root.getLabel() : "null";
		System.out.println("setting root with label: " + label);
		System.out.println("root: " + root);
		this.root = root;
	}

//	Rect bounds = new Rect();

	@Override
	protected void onDraw(Canvas canvas) {
//		canvas.getClipBounds(bounds);
		
//		System.out.println("onDraw with bounds : " + bounds);
		
		canvas.drawColor(Color.DKGRAY);	
		
		if (root != null) {
			root.draw(canvas);
//			Exception ex = new Exception();
//			ex.printStackTrace();
		} else {
			canvas.drawColor(Color.DKGRAY);	
		}
	}

	@Override
	public void invalidateRectF(final RectF bounds) {
		Rect rect = new Rect();
		bounds.round(rect);
		postInvalidate(rect.left, rect.top, rect.right, rect.bottom);
	}
}
