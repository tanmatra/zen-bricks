package zen.bricks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        int getBottom() {
            return y + height;
        }

        List<Brick> getChildren() {
            return children.subList(startIndex, endIndex);
        }

        public Iterator<Brick> iterator() {
            return getChildren().iterator();
        }

        /**
         * @param baseY screen coordinate of brick
         */
        boolean intersects(int baseY, Rectangle rect) {
            final int top = baseY + y;
            return (rect.y < (top + height)) && ((rect.y + rect.height) > top);
        }

        void paint(GC gc, int baseX, int baseY, Rectangle clipping,
                   Editor editor)
        {
            final int clipLeft = clipping.x - baseX;
            final int clipRight = clipLeft + clipping.width;
            int i;
            if ((endIndex - startIndex) <= BINSEARCH_THRESHOLD) {
                i = linearFindChild(clipLeft);
            } else {
                i = binaryFindChild(clipLeft);
            }
            for (; i < endIndex; i++) {
                final Brick child = children.get(i);
                if (child.x >= clipRight) {
                    break;
                }
                child.paint(gc, baseX + child.x, baseY + child.y,
                        clipping, editor);
            }
        }

        private int linearFindChild(int clipLeft) {
            for (int i = startIndex; i < endIndex; i++) {
                final Brick child = children.get(i);
                if (child.getRight() > clipLeft) {
                    return i;
                }
            }
            return endIndex;
        }

        private int binaryFindChild(int clipLeft) {
            int min = startIndex;
            int max = endIndex;
            while (min < max) {
                final int mid = (min + max) >>> 1;
                final Brick child = children.get(mid);
                if (child.getRight() <= clipLeft) {
                    min = mid + 1;
                } else {
                    max = mid;
                }
            }
            return min;
        }
    }

    // ============================================================ Class Fields

    private static final int BINSEARCH_THRESHOLD = 4;

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
        final int length = lines.size();
        final int clipTop = clipping.y - baseY;
        final int clipBottom = clipTop + clipping.height;
        int i;
        if (length <= BINSEARCH_THRESHOLD) {
            i = linearFindLine(clipTop, length);
        } else {
            i = binaryFindLine(clipTop, length);
        }
        for (; i < length; i++) {
            final Line line = lines.get(i);
            if (line.y >= clipBottom) {
                break;
            }
            line.paint(gc, baseX, baseY, clipping, editor);
        }
    }

    private int linearFindLine(int clipTop, int length) {
        for (int i = 0; i < length; i++) {
            final Line line = lines.get(i);
            if (line.getBottom() > clipTop) {
                return i;
            }
        }
        return length;
    }

    private int binaryFindLine(int clipTop, int length) {
        int min = 0;
        int max = length;
        while (min < max) {
            final int mid = (min + max) >>> 1;
            final Line line = lines.get(mid);
            if (line.getBottom() <= clipTop) {
                min = mid + 1;
            } else {
                max = mid;
            }
        }
        return min;
    }

    void calculateSize(UI ui, Editor editor) {
        ui.layout(this, editor);
    }

    public String toString() {
        return String.format(
                "TextBrick[@%H, parent=@%H, '%s', x=%d, y=%d, w=%d, h=%d, " +
                "index=%d, lines=%d, screen=%s]",
                this, parent, text, x, y, width, height,
                index, lines.size(), toScreen());
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
