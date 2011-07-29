package zen.bricks;

import org.eclipse.swt.graphics.Rectangle;

public abstract class Position
{
    // ========================================================== Nested Classes

    public static enum Side
    {
        LEFT,
        RIGHT
    }

    // ================================================================= Methods

    public abstract Brick getBrick();

    public abstract boolean next();

    public abstract boolean previous();

    public abstract Position preceding();

    public abstract Position following();

    public abstract void first();

    public abstract void last();

    public abstract Position up(Side side);

    public abstract Rectangle toScreen();

    // -------------------------------------------------------------------------
    public abstract boolean canEdit();

    public abstract boolean canInsert();

    public abstract boolean canDelete();

    public abstract boolean canBackDelete();

    // -------------------------------------------------------------------------
    public abstract void edit(Editor editor);

    public abstract void insert(Brick child);

    public abstract void delete(Editor editor);

    public abstract void backDelete(Editor editor);
}
