package jebl01.wally;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import jebl01.wally.panels.Panel;

public class RootSurfaceView extends SurfaceView implements RedrawListener, SurfaceHolder.Callback {

	private Panel root;
    private volatile boolean created = false;

	public RootSurfaceView(Context context) {
		super(context);
        getHolder().addCallback(this);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		System.out.println("onLayout!!!");
		if (root != null) {
			System.out.println("will layout!: " + changed + " " + left + " " + top + " " + right + " " + bottom);
			root.layout(new RectF(left, top, right, bottom));
		}

        redraw(new Rect(left, top, right, bottom));
	}

	public void setRootPanel(Panel root) {
		System.out.println("setting root");
		System.out.println("root: " + root);
		this.root = root;
	}

    @Override
    public void redraw(Rect bounds) {
        if(!created) return;

        final Canvas canvas = getHolder().lockCanvas(bounds);
        try {
//            System.out.println("drawView with bounds: " + bounds + " canvas clip=" + canvas.getClipBounds());
            this.root.paintInternal(canvas);
        } finally {
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("Surface onDraw!!");
        super.onDraw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("Surface created!!");
        this.created = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("Surface changed!!");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("Surface destroyed!!");
        this.created = false;
    }

}
