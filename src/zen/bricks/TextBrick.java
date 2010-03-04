package zen.bricks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

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
    final List<Brick> children = new ArrayList<Brick>();

    TextBrick(Brick parent, String text) {
        super(parent);
        this.text = text;
    }

    TextBrick(String text) {
        super(null);
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

    void paint(GC gc, int baseX, int baseY, UI ui) {
        super.paint(gc, baseX, baseY, ui);
        paintText(gc, baseX, baseY, ui);
        paintChildren(gc, baseX, baseY, ui);
    }

    void paintChildren(GC gc, int baseX, int baseY, UI ui) {
        for (final Brick brick : children) {
            brick.paint(gc, baseX + brick.x, baseY + brick.y, ui);
        }
    }

    private void paintText(GC gc, int baseX, int baseY, UI ui) {
        gc.setForeground(ui.getTextColor());
        gc.setBackground(ui.getTextBackColor());
        gc.drawText(text, baseX + TEXT_MARGIN_LEFT, baseY + TEXT_MARGIN_TOP,
                false);
    }

    void calculateSize(UI ui) {
        final Point textExtent = ui.getTextExtent(text);
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
        return "TextBrick['" + text + "', x=" + x + ", y=" + y + ", w=" + width
            + ", h=" + height + "]";
    }
}
