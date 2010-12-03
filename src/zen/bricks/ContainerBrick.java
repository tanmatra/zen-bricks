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

    public abstract Brick getFirstChild();

    protected boolean isValidIndex(int index) {
        return (index >= 0) && (index < childrenCount());
    }
}
