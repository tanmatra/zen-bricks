package zen.bricks;

import java.util.List;
import org.eclipse.swt.graphics.Rectangle;
import zen.bricks.TupleBrick.Line;

class TuplePosition extends Position
{
    /**
     * Allow positioning in text label?
     */
    private static final boolean ALLOW_ON_TEXT = true;

    protected final TupleBrick tuple;

    /*
     * [ txt ]
     *  ^   ^
     *  ?   |
     * -1   0
     *
     * [ txt ch0 ]
     *  ^   ^   ^
     *  ?   |   |
     * -1   0   1
     *
     * [ txt ch0 ch1 ch2 ]
     *  ^   ^   ^   ^   ^
     *  ?   |   |   |   |
     * -1   0   1   2   3
     */
    protected final int index;

    TuplePosition(TupleBrick tuple, int index) {
        this.tuple = tuple;
        this.index = index;
    }

    TuplePosition(TupleBrick tuple, Side side) {
        this.tuple = tuple;
        switch (side) {
            case LEFT:
                if (ALLOW_ON_TEXT) {
                    index = -1;
                } else {
                    index = 0;
                }
                break;
            case RIGHT:
                index = tuple.getChildCount();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Brick getBrick() {
        return tuple;
    }

    @Override
    public Position next() {
        if ((index < tuple.getChildCount())) {
            return new TuplePosition(tuple, index + 1);
        } else {
            return null;
        }
    }

    @Override
    public Position previous() {
        if (index > 0) {
            return new TuplePosition(tuple, index - 1);
        }
        if (index < 0) {
            return null;
        }
        // index == 0
        if (ALLOW_ON_TEXT) {
            return new TuplePosition(tuple, -1);
        } else {
            return null;
        }
    }

    @Override
    public Position preceding() {
        if (index < 0) {
            return up(Side.LEFT);
        }
        if (index == 0) {
            if (ALLOW_ON_TEXT) {
                return new TuplePosition(tuple, -1);
            } else {
                return up(Side.LEFT);
            }
        }
        final Brick left = tuple.getChild(index - 1);
        final Position leftPos = left.enter(Side.RIGHT);
        if (leftPos != null) {
            return leftPos;
        } else {
            return new TuplePosition(tuple, index - 1);
        }
    }

    @Override
    public Position following() {
        if (index >= tuple.getChildCount()) {
            return up(Side.RIGHT);
        }
        if (index < 0) {
            return new TuplePosition(tuple, 0);
        }
        final Brick right = tuple.getChild(index);
        final Position rightPos = right.enter(Side.LEFT);
        if (rightPos != null) {
            return rightPos;
        } else {
            return new TuplePosition(tuple, index + 1);
        }
    }

    @Override
    public Position first() {
        if (ALLOW_ON_TEXT) {
            return new TuplePosition(tuple, -1);
        } else {
            return new TuplePosition(tuple, 0);
        }
    }

    @Override
    public Position last() {
        return new TuplePosition(tuple, tuple.getChildCount());
    }

    @Override
    public Position up(Side side) {
        final ContainerBrick parent = tuple.getParent();
        if (parent != null) {
            return parent.positionOf(tuple, side);
        } else {
            return null;
        }
    }

    @Override
    public Rectangle toScreen() {
        if (index < 0) {
            // left side of text label
            final LabelRenderer labelRenderer = tuple.getLabelRenderer();
            final Rectangle rect = labelRenderer.toScreen();
//            rect.x += labelRenderer.getTextX();
//            rect.y += labelRenderer.getTextY();
            return rect;
        }
        if (index < tuple.getChildCount()) {
            // left side of the child
            return tuple.getChild(index).toScreen();
        }
        // there is nothing on the rigth side of position
        if (index == 0) {
            // left side of first line
            final Line firstLine = tuple.getLines().get(0);
            final Rectangle rect = tuple.toScreen();
            rect.x += firstLine.x;
            rect.y += firstLine.y;
            rect.width = 0;
            rect.height = firstLine.height;
            return rect;
        }
        final Brick lastChild = tuple.getChild(index - 1);
        if (lastChild instanceof LineBreak) {
            // left side of last line
            final List<Line> lines = tuple.getLines();
            final Line lastLine = lines.get(lines.size() - 1);
            final Rectangle rect = tuple.toScreen();
            rect.x += lastLine.x;
            rect.y += lastLine.y;
            rect.width = 0;
            rect.height = lastLine.height;
            return rect;
        }
        // else right border of last child
        final Rectangle rect = lastChild.toScreen();
        rect.x += rect.width;
        rect.width = 0;
        return rect;
    }

    // -------------------------------------------------------------------------
    @Override
    public boolean canEdit() {
        if (ALLOW_ON_TEXT) {
            return (index < 0);
        } else {
            return true;
        }
    }

    @Override
    public boolean canInsert() {
        return (index >= 0) && (index <= tuple.getChildCount());
    }

    @Override
    public boolean canDelete() {
        return (index >= 0) && (index < tuple.getChildCount());
    }

    @Override
    public boolean canBackDelete() {
        return (index > 0);
    }

    @Override
    public void edit(Editor editor) {
        tuple.edit(editor);
    }

    @Override
    public void insert(Brick child) {
        tuple.insertChild(index, child);
    }

    @Override
    public Position delete(Editor editor) {
        final Brick old = tuple.removeChild(index);
        old.detach(editor);
        editor.revalidate(tuple);
        return this;
    }

    @Override
    public Position backDelete(Editor editor) {
        final Brick old = tuple.removeChild(index - 1);
        old.detach(editor);
        editor.revalidate(tuple);
        return new TuplePosition(tuple, index - 1);
    }
}
