package zen.bricks;

import org.eclipse.swt.widgets.Event;
import zen.bricks.Position.Side;

public abstract class ContainerBrick extends Brick
{
    public ContainerBrick(ContainerBrick parent) {
        super(parent);
    }

    public abstract void appendChild(Brick child);

    public abstract void insertChild(int position, Brick child);

    public abstract Brick removeChild(int index);

    public abstract Brick getChild(int i);

    public abstract int getChildCount();

    public boolean isValidIndex(int index) {
        return (index >= 0) && (index < getChildCount());
    }

    public abstract boolean isValidInsertIndex(int position);

    public abstract boolean isValidDeleteIndex(int position);

    @Override
    public Brick getFirstChild() {
        return (getChildCount() < 1) ? null : getChild(0);
    }

    public Brick getLastChild() {
        return (getChildCount() < 1) ? null : getChild(getChildCount() - 1);
    }

    @Override
    public Brick getLastDescendantOrSelf() {
        if (getChildCount() < 1) {
            return super.getLastDescendantOrSelf();
        }
        return getChild(getChildCount() - 1).getLastDescendantOrSelf();
    }

    @Override
    public void attach(Editor editor) {
        super.attach(editor);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChild(i).attach(editor);
        }
    }

    @Override
    public void detach(Editor editor) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChild(i).detach(editor);
        }
        super.detach(editor);
    }

    protected abstract Brick findChildAt(int x, int y);

    @Override
    public Brick handleMouseEvent(int mouseX, int mouseY, Event event, Editor editor) {
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

    public abstract Position positionOf(Brick brick, Side side);
}
