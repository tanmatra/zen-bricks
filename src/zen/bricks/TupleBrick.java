package zen.bricks;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public class TupleBrick extends Brick
{
    // ========================================================== Nested Classes

    class Line implements Iterable<Brick>
    {
        int y;
        int height;
        int startIndex;
        int endIndex;

        Line(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public Iterator<Brick> iterator() {
            return children.subList(startIndex, endIndex).iterator();
        }
    }

    // ================================================================== Fields

    String text;
    Point textExtent;
    int textX;
    int textY;
    final ArrayList<Brick> children = new ArrayList<Brick>();
    final private ArrayList<Line> lines = new ArrayList<Line>(1);

    // ============================================================ Constructors

    TupleBrick(TupleBrick parent) {
        super(parent);
        lines.add(new Line(0, 0));
    }

    TupleBrick(TupleBrick parent, String text) {
        this(parent);
        this.text = text;
    }

    // ================================================================= Methods

    public void setText(String text) {
        this.text = text;
    }

    void realize(Editor editor) {
        super.realize(editor);
        for (final Brick brick : children) {
            brick.realize(editor);
        }
    }

    void dispose() {
        for (final Brick brick : children) {
            brick.dispose();
        }
        super.dispose();
    }

    void addChild(Brick child) {
        int endIndex = children.size();
        child.index = endIndex;
        children.add(child);
        endIndex++;

        final Line lastLine = lines.get(lines.size() - 1);
        lastLine.endIndex = endIndex;
    }

    void newLine() {
        final Line prevLine = lines.get(lines.size() - 1);
        final int endIndex = prevLine.endIndex;
        final Line line = new Line(endIndex, endIndex);
        lines.add(line);
        // ???
    }

    int childrenCount() {
        return children.size();
    }

    Brick getChild(int i) {
        return children.get(i);
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public boolean isList() {
        return !children.isEmpty();
    }

    @Override
    public void paint(GC gc, int baseX, int baseY, UI ui, Rectangle clipping) {
        paintBackground(gc, baseX, baseY, ui, clipping);
        paintText(gc, baseX, baseY, ui, clipping);
        paintChildren(gc, baseX, baseY, ui, clipping);
    }

    protected void paintBackground(GC gc, int baseX, int baseY, UI ui,
                                   Rectangle clipping)
    {
        final StyleChain chain = ui.getStyleChain(this);
        gc.setBackground(chain.getBackgroundColor());
//        gc.setBackground(ui.getBackgroundColor());
        gc.fillRectangle(baseX, baseY, this.getWidth(), this.getHeight());

        ui.getBorder().paint(gc, baseX, baseY, this, clipping);
    }

    private void paintText(GC gc, int baseX, int baseY, UI ui, Rectangle clipping) {
        final int textX = baseX + this.textX;
        final int textY = baseY + this.textY;
        if (!clipping.intersects(textX, textY, textExtent.x, textExtent.y)) {
            return;
        }
        ui.getStyleChain(this).paintText(gc, textX, textY, text); // ???
    }

    private void paintChildren(GC gc, int baseX, int baseY, UI ui,
                               Rectangle clipping)
    {
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
