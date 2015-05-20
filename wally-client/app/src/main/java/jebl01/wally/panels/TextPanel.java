package jebl01.wally.panels;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;

import jebl01.wally.panels.layout.Layout;

public class TextPanel extends Panel {
    private static int PADDING = 5;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
	private String text;
    private Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    public TextPanel(String text, Align align) {
        super(null, Layout.NONE);

		this.text = text;
        paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		paint.setTextAlign(align);
	}

    @Override
    public void layout(RectF bounds) {
        super.layout(bounds);
        calculateTextSize();
    }

    public void setText(String text) {
		this.text = text;
		calculateTextSize();
	}

    public String getText() {
        return this.text;
    }

    public int getAlpha() {
        return 255;
    }

    @Override
    public void paint(Canvas canvas) {
        float textHeight = this.fontMetrics.descent - this.fontMetrics.ascent;
        float calculatedPadding = (this.size.getHeight() - textHeight) / 2;
        float yPos = this.size.getHeight() - calculatedPadding - this.fontMetrics.descent;
        this.paint.setColor(textColor());
        this.paint.setAlpha(getAlpha());

		switch(this.paint.getTextAlign()) {
            case CENTER:
                canvas.drawText(this.getText(), this.size.getWidth() / 2, yPos, paint);
                break;
            case LEFT:
                canvas.drawText(this.getText(), PADDING, yPos, paint);
                break;
            case RIGHT:
                canvas.drawText(this.getText(), this.size.getWidth() - PADDING, yPos, paint);
		}
    }

	private void calculateTextSize() {
		if(this.getText() == null || this.getText().length() == 0) return;

		Rect textBounds = new Rect();
		this.paint.getTextBounds(this.getText(), 0, this.getText().length(), textBounds);

		float scaleY = (this.size.getHeight() - 2 * PADDING) / textBounds.height();
		float scaleX = (this.size.getWidth() - 2 * PADDING) / textBounds.width();

		this.paint.setTextSize(this.paint.getTextSize() * Math.min(scaleX, scaleY));

        this.fontMetrics = this.paint.getFontMetrics();
	}
}
