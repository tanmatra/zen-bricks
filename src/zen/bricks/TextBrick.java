package zen.bricks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public class TextBrick extends Brick
{
    // ================================================================== Fields

    String text;
    Point textExtent;
    int textY;
    final List<Brick> children = new ArrayList<Brick>();

    // ============================================================ Constructors

    TextBrick(TextBrick parent) {
        super(parent);
    }

    TextBrick(TextBrick parent, String text) {
        super(parent);
        this.text = text;
    }

    // ================================================================= Methods

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
        ui.layout(this);
    }

    public String toString() {
        return String.format(
                "TextBrick[@%H, parent=%H, '%s', x=%d, y=%d, w=%d, h=%d]",
                this, parent, text, x, y, width, height);
    }

    void childResized(Brick child) {
        // TODO
    }

    public Brick mouseDown(int mouseX, int mouseY, Event event) {
        final Brick child = findChildAt(mouseX, mouseY);
        if (child == null) {
            return super.mouseDown(mouseX, mouseY, event); // TODO
        } else {
            return child;
        }
    }

    protected Brick findChildAt(int x, int y) {
        for (final Brick brick : children) {
            if (brick.contains(x, y)) {
                return brick;
            }
        }
        return null;
    }
}
