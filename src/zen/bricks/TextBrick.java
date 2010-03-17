package zen.bricks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class TextBrick extends Brick
{
    private static final int LINE_SPACING = 1;
    private static final int SPACING = 2;

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
        final int textX = baseX + ui.getTextMarginLeft();
        final int textY = baseY + ui.getTextMarginTop();
        if (!clipping.intersects(textX, textY, textExtent.x, textExtent.y)) {
            return;
        }
        ui.paintText(gc, textX, textY, text);
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
        width = ui.getTextMarginLeft() + textExtent.x;
        int currX = width + SPACING;
        int currY = ui.getBrickPaddingTop();
        int currLineHeight = ui.getTextMarginTop() + textExtent.y;
        for (final Brick brick : children) {
            brick.calculateSize(ui);
            if (brick.isLineBreak()) {
                currX = ui.getBrickPaddingLeft();
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
        width += ui.getBrickPaddingRight();
        height = currY + currLineHeight + ui.getBrickPaddingBottom();
    }

    public String toString() {
        return String.format(
                "TextBrick[@%H, parent=%H, '%s', x=%d, y=%d, w=%d, h=%d]",
                this, parent, text, x, y, width, height);
    }

    void childResized(Brick child) {
        // todo
    }
}
