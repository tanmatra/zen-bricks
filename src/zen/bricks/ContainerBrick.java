package zen.bricks;

import org.eclipse.swt.widgets.Event;

public abstract class ContainerBrick extends Brick
{
    public ContainerBrick(ContainerBrick parent) {
        super(parent);
    }

    public abstract void appendChild(Brick child);

    public abstract void insertChild(int position, Brick child);

    public abstract Brick removeChild(int index);

    public abstract Brick getChild(int i);

    public abstract int childrenCount();

    public boolean isValidIndex(int index) {
        return (index >= 0) && (index < childrenCount());
    }

    public abstract boolean isValidInsertIndex(int position);

    public abstract boolean isValidDeleteIndex(int position);

    public Brick getFirstChild() {
        return (childrenCount() < 1) ? null : getChild(0);
    }

    public Brick getLastChild() {
        return (childrenCount() < 1) ? null : getChild(childrenCount() - 1);
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

    protected void checkChild(Brick child) {
        if (child.getParent() != this) {
            throw new RuntimeException("Wrong parent");
        }
    }
}
