package jebl01.wally.panels;

import android.graphics.Paint;

/**
 * Created by JesperBlomquist on 2015-03-19.
 */
public class Colors {
    private final Paint background;
    private final Paint light;
    private final Paint dark;
    private final Paint text;


    public Colors(Paint background, Paint light, Paint dark, Paint text) {
        this.background = background;
        this.light = light;
        this.dark = dark;
        this.text = text;
    }
}
