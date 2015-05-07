package jebl01.wally.controls;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Option;
import com.google.common.base.Function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jebl01.wally.controls.layout.Layout;


public abstract class Panel {
	protected RectF bounds = new RectF();
	private final Layout layoutEngine;
	private final EventListener eventListener;
	private Option<String> label;
	private Bitmap bitmap = null;
	private final Object lock = new Object();
	private final Paint bgPaint = new Paint();
	protected Rect size = new Rect();;
	private final Option<Text> labelText;
	
	protected List<Panel> children = new ArrayList<Panel>();

	protected Panel(Layout layoutEngine, EventListener eventListener, Option<String> label) {
		this.layoutEngine = layoutEngine;
		this.eventListener = eventListener;
		this.label = label;

		this.bgPaint.setColor(Color.BLACK);
		this.bgPaint.setStyle(Style.FILL);


        this.labelText = label.map(new Function<String, Text>(){
            public Text apply(String label) {
                return new Text(label, Align.LEFT);
            }
        });
	}
	
	
	public void draw(Canvas canvas) {
		if(canvas.quickReject(bounds, EdgeType.BW)) return;
		
		synchronized (lock) {
			canvas.drawBitmap(bitmap, size, bounds, null);			
		}

		for(Panel child : children) {
			child.draw(canvas);
		}
	}
	
	public void start() {
		for(Panel child : children) {
			child.start();
		}
	}
	
	protected void refreshDrawCache() {
		synchronized (lock) {
			Canvas canvas = new Canvas(this.bitmap);
			drawCached(canvas);			
		}
	}
	
	protected void drawCached(Canvas canvas){
		canvas.drawRect(size, getBackgroundPaint());
		for(Text text : this.labelText) {
            text.draw(canvas);
        }
	}
	
	protected Paint getBackgroundPaint() {
		return this.bgPaint;
	}
	
	public void setBounds(RectF bounds) {
		if(bounds.equals(this.bounds)) return;
		
		this.layoutEngine.layout(this, bounds);
		
		this.bounds = bounds;
		
		synchronized (lock) {
			this.bitmap = Bitmap.createBitmap((int)bounds.width(), (int)bounds.height(), Config.ARGB_8888);
			this.size = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		}

        for(Text text : this.labelText) {
            //layout text
            RectF labelBounds = new RectF(0, 0, this.size.width(), this.size.height() / 8);
            labelBounds.inset(5, 5); //padding

            text.setBounds(labelBounds);
        }
	}
	
	public void insert(Panel child) {
		this.children.add(child);
	}
	
	public void insert(Panel child, int index) {
		this.children.add(index, child);
	}
	
	public Collection<Panel> children() {
		return Collections.unmodifiableCollection(this.children);
	}
	
	public void invalidate(RectF bounds) {
		this.eventListener.invalidateRectF(bounds);
	}
	
	public String getLabel() {
        //return label != null ? label : "";
        return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		
		if(children.size() > 0) {
			sb.append('[');
			Iterator<Panel> childIterator = children.iterator();
			
			while(childIterator.hasNext()) {
				sb.append(childIterator.next());
				if(childIterator.hasNext()) {
					sb.append(", ");
				}
			}
			sb.append(']');
		}
		return sb.toString();
	}
	
	public void setLabel(String label) {

        //this.label = label;
	}
	
	protected Bitmap getBitmap() {
		return bitmap;
	}
}
