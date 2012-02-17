package zen.bricks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import zen.bricks.Position.Side;

public class TupleBrick extends ContainerBrick
{
    // ========================================================== Nested Classes

    class Line implements Iterable<Brick>
    {
        int x;
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

        void printDebugInfo() {
            System.out.format("(Line (start %d) (end %d))%n",
                    startIndex, endIndex);
        }
    }

    // ================================================================== Fields

    private String text;

    private LabelRenderer labelRenderer = new ScriptLabelRenderer(this);

    final ArrayList<Brick> children = new ArrayList<Brick>(2);

    private List<Line> lines;

    private boolean valid;

    private int contentCount;

    // ============================================================ Constructors

    public TupleBrick(ContainerBrick parent, String text) {
        super(parent);
        if (text == null) {
            throw new IllegalArgumentException("Null tuple text");
        }
        this.text = text;
    }

    // ================================================================= Methods

    public void attach(Editor editor) {
        super.attach(editor);
        labelRenderer.init(editor);
    }

    public void detach(Editor editor) {
        labelRenderer.dispose();
        super.detach(editor);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public LabelRenderer getLabelRenderer() {
        return labelRenderer;
    }

    public boolean isValidInsertIndex(int index) {
        return (index >= 0) && (index <= children.size());
    }

    public boolean isValidDeleteIndex(int index) {
        return (index >= 0) && (index < children.size());
    }

    public void appendChild(Brick child) {
        checkChild(child);
        final int childIndex = children.size();
        child.index = childIndex;
        children.add(child);
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
        final Brick old = children.remove(position);
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
        final StyleChain chain = editor.getStyleChain(this);

        final Border border = chain.find(TupleStyle.BORDER).getBorder();
        border.paintBackground(gc, baseX, baseY, this, clipping, editor);

        paintChildren(gc, baseX, baseY, clipping, editor);
        labelRenderer.paint(gc, baseX, baseY, clipping, editor);

        border.paintBorder(gc, baseX, baseY, this, clipping, editor);
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
        labelRenderer.invalidate();
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
        final StyleChain styleChain = editor.getStyleChain(this);
        final TupleLayout layout = styleChain.get(TupleStyle.LAYOUT);
        final boolean changed = layout.doLayout(this, editor);
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

    public Position enter(Side side) {
        return new TuplePosition(this, side);
    }

    public Position positionOf(Brick brick, Side side) {
        checkChild(brick);
        return new TuplePosition(this,
                brick.index + (side == Side.LEFT ? 0 : 1));
    }

    public Position locate(int cursorX, int cursorY) {
        // TODO: text
        final Brick child = findChildAt(cursorX, cursorY);
        if (child != null) {
            return positionOf(child, Side.LEFT);
        }
        return null;
    }

    public boolean edit(Editor editor) {
        final InputDialog dialog =
                new InputDialog(editor.getCanvas().getShell(), "Edit",
                        "Brick text:", getText(), null);
        if (dialog.open() == Window.CANCEL) {
            return false;
        }
        setText(dialog.getValue());
        editor.revalidate(this);
        return true;
    }

    public void printDebugInfo() {
        super.printDebugInfo();
        System.out.format("(count %d) (lines %d) (text: '%s')%n",
                getChildCount(), getLines().size(),
                text);
        for (final Line line : lines) {
            line.printDebugInfo();
        }
    }
}
