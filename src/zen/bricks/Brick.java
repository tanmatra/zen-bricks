package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public abstract class Brick
{
    TupleBrick parent;
    int index;
    int x;
    int y;
    int width;
    int height;
    int ascent;

    @Deprecated
    boolean lineBreak = true;

    Brick(TupleBrick parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public TupleBrick getParent() {
        return parent;
    }

    void realize(Editor editor) {
    }

    void dispose() {
    }

    public boolean contains(int x, int y) {
        return (x >= this.x) && (y >= this.y)
                && (x < (this.x + width)) && (y < (this.y + height));
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

    @Deprecated
    boolean isLineBreak() {
        return lineBreak;
    }

    @Deprecated
    int getAscent(UI ui) {
        return 0;
    }

    public Brick mouseDown(int mouseX, int mouseY, Event event) {
        // debugMouseEvent(event);
        return null;
    }

    @SuppressWarnings("unused")
    private void debugMouseEvent(Event event) {
        final String type;
        switch (event.type) {
            case SWT.MouseDoubleClick: type = "MouseDoubleClick"; break;
            case SWT.MouseDown: type = "MouseDown"; break;
            case SWT.MouseHover: type = "MouseHover"; break;
            case SWT.MouseMove: type = "MouseMove"; break;
            case SWT.MouseUp: type = "MouseUp"; break;
            default: type = "???"; break;
        }
        System.out.println("\nmouse event: " + event
                + "\ntype: " + type
                + "\non: " + this);
    }
}
