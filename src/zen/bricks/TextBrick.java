package zen.bricks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class TextBrick extends Brick
{
    private static final int LINE_SPACING = 1;
    private static final int PADDING_LEFT = 18;
    private static final int PADDING_TOP = 2;
    private static final int PADDING_RIGHT = 2;
    private static final int PADDING_BOTTOM = 2;
    private static final int SPACING = 2;
    private static final int TEXT_MARGIN_TOP = 2;
    private static final int TEXT_MARGIN_LEFT = 2;

    String text;
    private Point textExtent;
    final List<Brick> children = new ArrayList<Brick>();

    TextBrick(TextBrick parent) {
        super(parent);
    }

    TextBrick(TextBrick parent, String text) {
        super(parent);
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    void realize(UI ui) {
        super.realize(ui);
        for (final Brick brick : children) {
            brick.realize(ui);
        }
    }

    void dispose() {
        for (final Brick brick : children) {
            brick.dispose();
        }
        super.dispose();
    }

    void addChild(Brick child) {
        child.index = children.size();
        children.add(child);
    }

    int getAscent(UI ui) {
        int ascent = ui.getTextAscent();
        for (final Brick brick : children) {
            if (brick.isLineBreak()) {
                break;
            }
            final int childAscent = brick.getAscent(ui);
            ascent = Math.max(ascent, childAscent);
        }
        return ascent;
    }

    @Override
    void paint(GC gc, int baseX, int baseY, UI ui, Rectangle clipping) {
        super.paint(gc, baseX, baseY, ui, clipping);
        paintText(gc, baseX, baseY, ui, clipping);
        paintChildren(gc, baseX, baseY, ui, clipping);
    }

    private void paintText(GC gc, int baseX, int baseY, UI ui, Rectangle clipping) {
        final int textX = baseX + TEXT_MARGIN_LEFT;
        final int textY = baseY + TEXT_MARGIN_TOP;
        if (!clipping.intersects(textX, textY, textExtent.x, textExtent.y)) {
            return;
        }
        gc.setForeground(ui.getTextColor());
        gc.setBackground(ui.getTextBackColor());
        gc.drawText(text, textX, textY, false);
    }

    void paintChildren(GC gc, int baseX, int baseY, UI ui, Rectangle clipping) {
        for (final Brick brick : children) {
            final int brickX = baseX + brick.x;
            final int brickY = baseY + brick.y;
            if (!clipping.intersects(brickX, brickY, brick.width, brick.height))
            {
                continue;
            }
            brick.paint(gc, brickX, brickY, ui, clipping);
        }
    }

    void calculateSize(UI ui) {
        textExtent = ui.getTextExtent(text);
        width = TEXT_MARGIN_LEFT + textExtent.x;
        int currX = width + SPACING;
        int currY = PADDING_TOP;
        int currLineHeight = TEXT_MARGIN_TOP + textExtent.y;
        for (final Brick brick : children) {
            brick.calculateSize(ui);
            if (brick.isLineBreak()) {
                currX = PADDING_LEFT;
                currY += currLineHeight + LINE_SPACING;
                brick.x = currX;
                brick.y = currY;
                currLineHeight = brick.height;
            } else {
                currLineHeight = Math.max(currLineHeight, brick.height);
                currX += SPACING;
                // currY is unchanged
                brick.x = currX;
                brick.y = currY;
                currX += brick.width;
            }
            width = Math.max(width, brick.x + brick.width);
        }
        width += PADDING_RIGHT;
        height = currY + currLineHeight + PADDING_BOTTOM;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append("TextBrick[@").append(System.identityHashCode(this));
        buf.append(", parent=@");
        if (parent != null) {
            buf.append(System.identityHashCode(parent));
        } else {
            buf.append("null");
        }
        buf.append(", '").append(text);
        buf.append("', x=").append(x);
        buf.append(", y=").append(y);
        buf.append(", w=").append(width);
        buf.append(", h=").append(height);
        buf.append("]");
        return buf.toString();
    }

    void childResized(Brick child) {
        // todo
    }
}
