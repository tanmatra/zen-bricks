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
    int ascent;
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

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    boolean isLineBreak() {
        return lineBreak;
    }

    @Deprecated
    int getAscent(UI ui) {
        return 0;
    }
}
