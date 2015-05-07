package jebl01.wally;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by JesperBlomquist on 2015-03-19.
 */
public class PanelTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("creating panel test activity!!");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        TestView testView = new TestView(this);
        testView.setVisibility(View.VISIBLE);
        testView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(testView);

        testView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = (int) (Math.random() * v.getWidth() - 100);
                int y = (int) (Math.random() * v.getHeight() - 100);

                Rect bounds = new Rect(x, y, x + 100, y + 100);
                System.out.println("invalidating with bounds : " + bounds);
                ((TestView)v).drawView(bounds);
            }
        });
    }

    public static class TestView extends TextureView implements Runnable {
        private ScheduledThreadPoolExecutor animatorExecutor;
        Paint paint1 = new Paint();
        Paint paint2 = new Paint();
        Paint paint3 = new Paint();

        public TestView(Context context) {
            super(context);

            System.out.println("creating test view!!");

            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            this.animatorExecutor = new ScheduledThreadPoolExecutor(10);

            //this.animatorExecutor.scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
            paint1.setColor(Color.BLUE);
            paint1.setStyle(Paint.Style.FILL);
            paint2.setColor(Color.RED);
            paint2.setStyle(Paint.Style.FILL);
            paint3.setColor(Color.CYAN);
            paint3.setStyle(Paint.Style.FILL);
        }

        private Paint getPaint() {
            switch((int)Math.round(Math.random() * 2)) {
                case 0:
                    return paint1;
                case 1:
                    return paint2;
                case 2:
                    return paint3;
                default:
                    return paint1;
            }
        }

        public void drawView(Rect bounds) {

            Canvas canvas = lockCanvas(bounds);
            //canvas.clipRect(bounds);
            try {
                System.out.println("drawView with bounds: " + bounds + " canvas clip=" + canvas.getClipBounds());
                canvas.drawRect(bounds, getPaint());

            } finally {
                unlockCanvasAndPost(canvas);
            }


        }

//        public void invalidateRect(final Rect bounds) {
//            System.out.println("invalidating the rect!!");
//            postInvalidate(bounds.left, bounds.top, bounds.right, bounds.bottom);
//        }

        @Override
        public void run() {
            System.out.println("run!!");
            int x = (int)(Math.random() * this.getWidth() - 100);
            int y = (int)(Math.random() * this.getHeight() - 100);


            drawView(new Rect(x, y, x + 100, y + 100));
        }
    }
}
