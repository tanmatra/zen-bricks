package zen.bricks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class TextBrick extends Brick
{
    private static final int TEXT_MARGIN_TOP = 2;
    private static final int TEXT_MARGIN_LEFT = 2;
    private static final int LPAD = 18;

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
        final Point textExtent = ui.getGC().textExtent(text,
                SWT.DRAW_DELIMITER | SWT.DRAW_TAB);
//        System.out.println(extent);
        int innerWidth = TEXT_MARGIN_LEFT + textExtent.x;
        int currY = TEXT_MARGIN_TOP + textExtent.y + 1; // extra 1 pixel for text bug?
        // todo
        for (final Brick brick : children) {
            brick.calculateSize(ui);
            brick.x = LPAD;
            currY += 1;
            brick.y = currY;
            currY += brick.height;
            innerWidth = Math.max(innerWidth, brick.x + brick.width);
        }
        width = innerWidth + 2;
        height = currY + 2;
    }
}
