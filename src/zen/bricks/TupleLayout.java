package zen.bricks;

public abstract class TupleLayout
{
    protected UI ui;

    TupleLayout(UI ui) {
        this.ui = ui;
    }

    abstract void doLayout(TupleBrick brick, Editor editor);
}
