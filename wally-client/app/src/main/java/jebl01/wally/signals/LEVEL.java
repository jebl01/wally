package jebl01.wally.signals;

import android.graphics.Color;
import android.graphics.Paint;

public enum LEVEL {
    OK(Color.BLACK, Color.GRAY, Color.GREEN),
    UNKNOWN(Color.BLACK, Color.GRAY, Color.GRAY),
    WARNING(Color.YELLOW, Color.DKGRAY, Color.DKGRAY),
    CRITICAL(Color.RED, Color.DKGRAY, Color.DKGRAY);

    private final int bgColor;
    private final int textColor;
    private final int drawColor;

    private LEVEL(int bgColor, int textColor, int drawColor) {
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.drawColor = drawColor;
    }

    public int getBackgroundColor() {
        return this.bgColor;
    }
    public int getTextColor() {
        return this.textColor;
    }
    public int getDrawColor() {
        return this.drawColor;
    }
}
