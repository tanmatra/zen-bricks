package zen.bricks;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public abstract class Brick
{
    TextBrick parent;
    int index;
    int x;
    int y;
    int width;
    int height;
    boolean lineBreak = true;

    Brick(TextBrick parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public TextBrick getParent() {
        return parent;
    }

    void realize(UI ui) {
    }

    void dispose() {
    }

    void paint(GC gc, int baseX, int baseY, UI ui, Rectangle clipping) {
        ui.paintBackground(gc, this, baseX, baseY, clipping);
    }

    void calculateSize(UI ui) {
        // todo
    }

    boolean isLineBreak() {
        return lineBreak;
    }

    int getAscent(UI ui) {
        return 0;
    }
}
