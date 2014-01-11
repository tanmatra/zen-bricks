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

    public abstract Position next();

    public abstract Position previous();

    public abstract Position preceding();

    public abstract Position following();

    public abstract Position first();

    public abstract Position last();

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

    public abstract Position delete(Editor editor);

    public abstract Position backDelete(Editor editor);
}
