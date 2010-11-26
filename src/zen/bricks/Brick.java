package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public abstract class Brick
{
    final ContainerBrick parent;
    int index;
    int x;
    int y;
    int width;
    int height;
    int ascent;

    protected Brick(ContainerBrick parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public ContainerBrick getParent() {
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

    public abstract void paint(GC gc, int baseX, int baseY, UI ui,
                               Rectangle clipping, Editor editor);

    void calculateSize(UI ui, Editor editor) {
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
    int getAscent(UI ui) {
        return 0;
    }

    public Brick mouseEvent(int mouseX, int mouseY, Event event, Editor editor) {
        // debugMouseEvent(event);
        if (event.type == SWT.MouseDown) {
            editor.setSelection(this);
        }
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
