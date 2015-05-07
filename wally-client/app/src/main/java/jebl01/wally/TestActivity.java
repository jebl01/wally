package jebl01.wally;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.atlassian.fugue.Option;

import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.panels.Panel;
import jebl01.wally.panels.Column;
import jebl01.wally.panels.LabelDecorator;
import jebl01.wally.panels.Row;
import jebl01.wally.panels.contentPanels.ColorPanel;
import jebl01.wally.panels.contentPanels.SideBySideGraph;
import jebl01.wally.signals.Signal;


public class TestActivity extends Activity {

    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("creating test activity!");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        RootSurfaceView rootSurfaceView = new RootSurfaceView(this);
        rootSurfaceView.setVisibility(View.VISIBLE);
        rootSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(rootSurfaceView);


        Panel row = new Row(rootSurfaceView)
                .addChild(new LabelDecorator(rootSurfaceView, "Panel 1-1", LabelDecorator.LabelPosition.BELOW, new ColorPanel(rootSurfaceView, Color.RED)))
                .addChild(new LabelDecorator(rootSurfaceView, "Panel 1-2", LabelDecorator.LabelPosition.BELOW, Paint.Align.RIGHT, new ColorPanel(rootSurfaceView, Color.YELLOW)));

        DataProvider dataProvider = new DataProvider("test", DataProvider.FREQUENCY.SECOND);
        DataProvider dataProvider2 = new DataProvider("test2", DataProvider.FREQUENCY.SECOND);

        Panel graphPanel = new SideBySideGraph(rootSurfaceView, Arrays.asList(dataProvider, dataProvider2), Signal.OK, executor, Option.some(100));

        Panel rootPanel = new Column(rootSurfaceView)
                .addChild(new LabelDecorator(rootSurfaceView, "Panel g1", LabelDecorator.LabelPosition.ABOVE, row))
                .addChild(new ColorPanel(rootSurfaceView, Color.GREEN))
                .addChild(new LabelDecorator(rootSurfaceView, "Panel j3", LabelDecorator.LabelPosition.ABOVE, Paint.Align.CENTER, graphPanel));


        rootSurfaceView.setRootPanel(rootPanel);
        rootPanel.start();

//         testView = new TestView(this);
//        testView.setVisibility(View.VISIBLE);
//        testView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        setContentView(testView);
//
//        testView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int x = (int) (Math.random() * v.getWidth() - 500);
//                int y = (int) (Math.random() * v.getHeight() - 500);
//
//                Rect bounds = new Rect(x, y, x + 500, y + 500);
//                System.out.println("invalidating with bounds : " + bounds);
//                ((TestView)v).drawView(bounds);
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

}
