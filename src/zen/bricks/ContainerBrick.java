package zen.bricks;

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
}
