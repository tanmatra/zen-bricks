package zen.bricks;

import org.eclipse.swt.widgets.Event;

public abstract class ContainerBrick extends Brick
{
    public ContainerBrick(ContainerBrick parent) {
        super(parent);
    }

    protected abstract void addChild(Brick child);

    protected abstract Brick getChild(int i);

    protected abstract int childrenCount();

    protected abstract void childResized(Brick child);

    protected boolean isValidIndex(int index) {
        return (index >= 0) && (index < childrenCount());
    }

    public Brick getFirstChild() {
        return (childrenCount() < 1) ? null : getChild(0);
    }

    public Brick getLastDescendantOrSelf() {
        if (childrenCount() < 1) {
            return super.getLastDescendantOrSelf();
        }
        return getChild(childrenCount() - 1).getLastDescendantOrSelf();
    }

    public void attach(Editor editor) {
        super.attach(editor);
        final int count = childrenCount();
        for (int i = 0; i < count; i++) {
            getChild(i).attach(editor);
        }
    }

    public void detach(Editor editor) {
        final int count = childrenCount();
        for (int i = 0; i < count; i++) {
            getChild(i).detach(editor);
        }
        super.detach(editor);
    }

    protected abstract Brick findChildAt(int x, int y);

    public Brick handleMouseEvent(int mouseX, int mouseY, Event event,
                                  Editor editor)
    {
        final Brick child = findChildAt(mouseX, mouseY);
        if (child == null) {
            return super.handleMouseEvent(mouseX, mouseY, event, editor);
        } else {
            return child;
        }
    }
}
