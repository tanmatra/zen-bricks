package zen.bricks;

public abstract class TupleLayout
{
    protected TupleLayout() {
    }

    /**
     * @param brick
     * @param editor
     * @return <code>true</code> if brick has really changed its size
     */
    public abstract boolean doLayout(TupleBrick brick, Editor editor);

    public abstract String getTitle();

    public abstract String getName();
}
