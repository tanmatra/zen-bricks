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

        Line(int startIndex, int endIndex, int y, int height) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.y = y;
            this.height = height;
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

    private String text;

    Point textExtent;

    int textX;

    int textY;

    final ArrayList<Brick> children = new ArrayList<Brick>(2);

    private List<Line> lines;

    private boolean valid;

    private int contentCount;

    // ============================================================ Constructors

    public TupleBrick(ContainerBrick parent) {
        super(parent);
        children.add(new LineBreak(this));
    }

    public TupleBrick(ContainerBrick parent, String text) {
        this(parent);
        this.text = text;
    }

    // ================================================================= Methods

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public boolean isValidInsertIndex(int position) {
        return (position >= 0) && (position < children.size());
    }

    public boolean isValidDeleteIndex(int position) {
        return (position >= 0) && (position < children.size() - 1);
    }

    public void appendChild(Brick child) {
        checkChild(child);
        final int childIndex = children.size() - 1;
        children.get(childIndex).index++; // for final line break
        child.index = childIndex;
        children.add(childIndex, child);
        if (!(child instanceof LineBreak)) {
            contentCount++;
        }
        invalidate();
    }

    private void reindex(int startIndex) {
        final int count = children.size();
        for (int i = startIndex; i < count; i++) {
            children.get(i).index = i;
        }
    }

    public void insertChild(int position, Brick child) {
        checkChild(child);
        if (!isValidInsertIndex(position)) {
            throw new RuntimeException("Invalid insert index: " + position);
        }
        children.add(position, child);
        reindex(position);
        if (!(child instanceof LineBreak)) {
            contentCount++;
        }
        invalidate();
    }

    public Brick removeChild(int position) {
        if (!isValidDeleteIndex(position)) {
            throw new RuntimeException("Invalid delete index:" + position);
        }
        final Brick old = children.get(position);
        children.remove(position);
        reindex(position);
        if (!(old instanceof LineBreak)) {
            contentCount--;
        }
        invalidate();
        return old;
    }

    public void newLine() {
        appendChild(new LineBreak(this));
    }

    void setLines(List<Line> lines) {
        this.lines = lines;
    }

    List<Brick> getChildrenList() {
        return children;
    }

    public int getChildCount() {
        return children.size();
    }

    public Brick getChild(int i) {
        return children.get(i);
    }

    public List<Line> getLines() {
        return lines;
    }

    public int getContentCount() {
        return contentCount;
    }

    public boolean isList() {
        return getContentCount() > 0;
    }

    @Override
    public void paint(GC gc, int baseX, int baseY, Rectangle clipping,
                      Editor editor)
    {
        final UI ui = editor.getUI();
        final StyleChain chain = ui.getStyleChain(this, editor);

        final Border border = chain.find(TupleStyle.BORDER).getBorder();
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

    protected void invalidate() {
        valid = false;
    }

    public void invalidate(boolean all) {
        super.invalidate(all);
        invalidate();
        if (all) {
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChild(i).invalidate(all);
            }
        }
    }

    public boolean doLayout(Editor editor, boolean force) {
        if (valid && !force) {
            return false;
        }
        final StyleChain styleChain = editor.getUI().getStyleChain(this, editor);
        final TupleStyle style = styleChain.find(TupleStyle.LAYOUT);
        final boolean changed = style.getLayout().doLayout(this, editor);
        valid = true;
        return changed;
    }

    public String toString() {
        return String.format(
                "(TextBrick \"%s\" (%d %d %d %d) (index %d))",
                text, x, y, getWidth(), getHeight(), index);
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
