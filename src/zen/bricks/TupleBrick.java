package zen.bricks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

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
            for (int i = findChild(clipLeft); i < endIndex; i++) {
                final Brick child = children.get(i);
                if (child.x >= clipRight) {
                    break;
                }
                child.paint(gc, baseX + child.x, baseY + child.y,
                        clipping, editor);
            }
        }

        int findChild(int clipLeft) {
            int min = startIndex;
            int max = endIndex;
            while (min < max) {
                final int mid = (min + max) >>> 1;
                if (children.get(mid).getRight() <= clipLeft) {
                    min = mid + 1;
                } else {
                    max = mid;
                }
            }
            return min;
        }

        public String toString() {
            return String.format(
                    "(Line [y %d] [height %d] [start %d] [end %d])",
                    y, height, startIndex, endIndex);
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

        final Border border = TupleStyle.BORDER.find(chain).getBorder();
        border.paint(gc, baseX, baseY, this, clipping, editor);

        paintText(gc, baseX, baseY, clipping, chain);
        paintChildren(gc, baseX, baseY, clipping, editor);
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

        final TupleStyle backgroundStyle = chain.findTextBackground();
        final Color color = backgroundStyle.getTextBackground().getColor();
        int flags = StyleChain.TEXT_FLAGS;
        if (color != null) {
            gc.setBackground(color);
        } else {
            flags |= SWT.DRAW_TRANSPARENT;
        }

        gc.drawText(text, textX, textY, flags);
    }

    private void paintChildren(GC gc, int baseX, int baseY, Rectangle clipping,
                               Editor editor)
    {
        final int length = lines.size();
        final int clipTop = clipping.y - baseY;
        final int clipBottom = clipTop + clipping.height;
        for (int i = findLine(clipTop); i < length; i++) {
            final Line line = lines.get(i);
            if (line.y >= clipBottom) {
                break;
            }
            line.paint(gc, baseX, baseY, clipping, editor);
        }
    }

    private int findLine(int clipTop) {
        int min = 0;
        int max = lines.size();
        while (min < max) {
            final int mid = (min + max) >>> 1;
            if (lines.get(mid).getBottom() <= clipTop) {
                min = mid + 1;
            } else {
                max = mid;
            }
        }
        return min;
    }

    protected boolean doLayout(Editor editor) {
        final StyleChain styleChain = editor.getUI().getStyleChain(this, editor);
        final TupleStyle style = TupleStyle.LAYOUT.find(styleChain);
        return style.getLayout().doLayout(this, editor);
    }

    public String toString() {
        return String.format(
                "TextBrick[@%H, parent=@%H, '%s', x=%d, y=%d, w=%d, h=%d, " +
                "index=%d, lines=%d, screen=%s]",
                this, parent, text, x, y, getWidth(), getHeight(),
                index, lines.size(), toScreen());
    }

    protected void childResized(Brick child) {
        // TODO
    }

    protected Brick findChildAt(int x, int y) {
        final int lineIdx = findLine(y);
        if (lineIdx >= lines.size()) {
            return null;
        }
        final Line line = lines.get(lineIdx);
        if (y < line.y) {
            return null;
        }
        final int childIdx = line.findChild(x);
        if (childIdx >= line.endIndex) {
            return null;
        }
        final Brick child = children.get(childIdx);
        if ((x < child.x) || (y < child.y) || (y >= child.getBottom())) {
            return null;
        }
        return child;
    }
}
