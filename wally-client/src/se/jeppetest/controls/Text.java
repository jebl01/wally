package se.jeppetest.controls;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;

public class Text{
	
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
	private String text;
	private RectF bounds;
	
	public Text(String text, Align align) {
		
		this.text = text;
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		paint.setTextAlign(align);
	}
	
	public void setBounds(RectF bounds) {
		this.bounds = bounds;
		calculateTextSize();
	}
	
	public void setText(String text) {
		this.text = text;
		calculateTextSize();
	}
	
	public void draw(Canvas canvas) {
		Rect textSize = getTextSize();
		float bottom = bounds.bottom - ((bounds.height() - textSize.height()) / 2);
		
		switch(this.paint.getTextAlign()) {
		case CENTER:
			canvas.drawText(this.text, bounds.left + bounds.width() / 2, bottom, paint);
			break;
		case LEFT:
			canvas.drawText(this.text, bounds.left, bottom, paint);
			break;
		case RIGHT:
			canvas.drawText(this.text, bounds.left + bounds.width() - textSize.width(), bottom, paint);			
		}
	}
	
	private Rect getTextSize() {
		Rect textBounds = new Rect();
		this.paint.getTextBounds(this.text, 0, this.text.length(), textBounds);
		return textBounds;
	}
	
	private void calculateTextSize() {
		if(this.text == null || this.text.length() == 0 || this.bounds == null) return;
		
		Rect textBounds = new Rect();
		this.paint.getTextBounds(this.text, 0, this.text.length(), textBounds);
		
		float scaleY = this.bounds.height() / textBounds.height();
		float scaleX = this.bounds.width() / textBounds.width();
		
		this.paint.setTextSize(this.paint.getTextSize() * Math.min(scaleX, scaleY));
	}

}
