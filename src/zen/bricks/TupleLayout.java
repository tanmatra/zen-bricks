package zen.bricks;

public abstract class TupleLayout
{
    protected TupleLayout() {
    }

    public abstract void doLayout(TupleBrick brick, Editor editor);

    public abstract String getTitle();

    public abstract String getName();
}
