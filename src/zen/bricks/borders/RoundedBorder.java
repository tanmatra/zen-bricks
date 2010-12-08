package zen.bricks.borders;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import zen.bricks.Border;
import zen.bricks.BorderFactory;
import zen.bricks.Brick;
import zen.bricks.UI;

public class RoundedBorder extends Border
{
    public static class Factory extends BorderFactory
    {
        public Border createBorder(UI ui) {
            return new RoundedBorder(this, ui);
        }

        public String getName() {
            return "rounded";
        }

        public String getTitle() {
            return "Rounded";
        }
    }

    private int arcSize;

    protected RoundedBorder(BorderFactory factory, UI ui) {
        super(factory, ui);
    }

    public void load(Preferences preferences) {
        // TODO Somehow initialize arc size
        // super.load(preferences);
        arcSize = preferences.getInt("arcSize", 0);
    }

    protected void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping)
    {
        gc.drawRoundRectangle(x, y,
                brick.getWidth() - 1, brick.getHeight() - 1,
                arcSize, arcSize);
    }
}
