package zen.bricks;

public abstract class TupleLayout
{
    protected TupleLayout() {
    }

    public abstract void doLayout(TupleBrick brick, Editor editor);
}
