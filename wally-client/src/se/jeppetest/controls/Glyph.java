package se.jeppetest.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import se.jeppetest.controls.layout.Layout;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas.EdgeType;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class Glyph {
	protected RectF bounds = new RectF();
	private final Layout layoutEngine;
	private final EventListener eventListener;
	private String label;
	private Bitmap bitmap = null;
	private final Object lock = new Object();
	private final Paint bgPaint = new Paint();
	protected Rect size = new Rect();;
	private final Text labelText;
	
	protected List<Glyph> children = new ArrayList<Glyph>();

	protected Glyph(Layout layoutEngine, EventListener eventListener, String label) {
		this.layoutEngine = layoutEngine;
		this.eventListener = eventListener;
		this.label = label;

		this.bgPaint.setColor(Color.BLACK);
		this.bgPaint.setStyle(Style.FILL);
		
		this.labelText = new Text(label, Align.LEFT);
	}
	
	
	public void draw(Canvas canvas) {
		if(canvas.quickReject(bounds, EdgeType.BW)) return;
		
		synchronized (lock) {
			canvas.drawBitmap(bitmap, size, bounds, null);			
		}

		for(Glyph child : children) {
			child.draw(canvas);
		}
	}
	
	public void start() {
		for(Glyph child : children) {
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
		this.labelText.draw(canvas);
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
		
		//layout text
		RectF labelBounds = new RectF(0, 0, this.size.width(), this.size.height() / 8);
		labelBounds.inset(5, 5); //padding
		this.labelText.setBounds(labelBounds);
	}
	
	public void insert(Glyph child) {
		this.children.add(child);
	}
	
	public void insert(Glyph child, int index) {
		this.children.add(index, child);
	}
	
	public Collection<Glyph> children() {
		return Collections.unmodifiableCollection(this.children);
	}
	
	public void invalidate(RectF bounds) {
		this.eventListener.invalidateRectF(bounds);
	}
	
	public String getLabel() {
		return label != null ? label : "";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		
		if(children.size() > 0) {
			sb.append('[');
			Iterator<Glyph> childIterator = children.iterator();
			
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
		this.label = label;
	}
	
	protected Bitmap getBitmap() {
		return bitmap;
	}
}
