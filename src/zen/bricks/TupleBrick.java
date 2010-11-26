package zen.bricks;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public class TupleBrick extends ContainerBrick
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

        /**
         * @param baseY screen coordinate of brick
         */
        boolean intersects(int baseY, Rectangle rect) {
            final int top = baseY + y;
            return (rect.y < (top + height)) && ((rect.y + rect.height) > top);
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

    protected void addChild(Brick child) {
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

    protected int childrenCount() {
        return children.size();
    }

    protected Brick getChild(int i) {
        return children.get(i);
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public boolean isList() {
        return !children.isEmpty();
    }

    @Override
    public void paint(GC gc, int baseX, int baseY, Rectangle clipping,
                      Editor editor)
    {
        final UI ui = editor.getUI();
        final StyleChain chain = ui.getStyleChain(this, editor);
        paintBackground(gc, baseX, baseY, ui, clipping, chain);
        paintText(gc, baseX, baseY, clipping, chain);
        paintChildren(gc, baseX, baseY, ui, clipping, editor);
    }

    private void paintBackground(GC gc, int baseX, int baseY, UI ui,
                                 Rectangle clipping, StyleChain chain)
    {
        gc.setBackground(chain.getBackgroundColor());
        gc.fillRectangle(baseX, baseY, this.getWidth(), this.getHeight());

        ui.getBorder().paint(gc, baseX, baseY, this, clipping);
    }

    private void paintText(GC gc, int baseX, int baseY, Rectangle clipping,
                           StyleChain chain)
    {
        final int textX = baseX + this.textX;
        final int textY = baseY + this.textY;
        if (!clipping.intersects(textX, textY, textExtent.x, textExtent.y)) {
            return;
        }

        gc.setFont(chain.getFont());
        gc.setForeground(chain.getForegroundColor());

        final TupleStyle background = chain.findTextBackground();
        int flags = StyleChain.TEXT_FLAGS;
        if (background.textBackground) { // garanteed not null here
            gc.setBackground(background.getTextBackgroundColor());
        } else {
            flags |= SWT.DRAW_TRANSPARENT;
        }

        gc.drawText(text, textX, textY, flags);
    }

    private void paintChildren(GC gc, int baseX, int baseY, UI ui,
                               Rectangle clipping, Editor editor)
    {
        for (final Line line : lines) {
            if (line.intersects(baseY, clipping)) {
                for (final Brick brick : line) {
                    brick.repaint(gc, baseX, baseY, clipping, editor);
                }
            }
        }
    }

    void calculateSize(UI ui, Editor editor) {
        ui.layout(this, editor);
    }

    public String toString() {
        return String.format(
                "TextBrick[@%H, parent=%H, '%s', x=%d, y=%d, w=%d, h=%d]",
                this, parent, text, x, y, width, height);
    }

    protected void childResized(Brick child) {
        // TODO
    }

    public Brick mouseEvent(int mouseX, int mouseY, Event event, Editor editor) {
        final Brick child = findChildAt(mouseX, mouseY);
        if (child == null) {
            return super.mouseEvent(mouseX, mouseY, event, editor); // TODO
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
