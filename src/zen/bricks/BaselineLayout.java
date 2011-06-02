package zen.bricks;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;

import zen.bricks.TupleBrick.Line;

public class BaselineLayout extends TupleLayout
{
    private static final boolean DEBUG = true;

    private int indent;

    public BaselineLayout() {
    }

    @SuppressWarnings("unused")
    private void debug(String fmt, Object... args) {
        if (!DEBUG) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        System.out.format(fmt, args);
    }

    public String getTitle() {
        return "Base line";
    }

    public String getName() {
        return "baseline";
    }

    public boolean doLayout(TupleBrick brick, Editor editor) {
        debug("BEGIN %s%n", brick);
        indent++;

        final UI ui = editor.getUI();
        final StyleChain chain = ui.getStyleChain(brick, editor);
        final Margin textMargin = chain.getTextMargin();
        final Margin brickPadding = chain.getPadding();
        final int paddingLeft = brickPadding.getLeft();
        final int lineSpacing = chain.getLineSpacing();
        final int spacing = chain.getSpacing();

        final ArrayList<Line> lines = new ArrayList<TupleBrick.Line>(1);

        final Point textExtent = chain.getTextExtent(brick.getText());
        debug("Extent '%s' = %d x %d%n", brick.getText(),
                textExtent.x, textExtent.y);
        brick.textX = textMargin.getLeft();
        brick.textExtent = textExtent;
        final int textAscent = chain.getTextAscent();
        int lineAscent = textAscent;

        int lineY = brickPadding.getTop();
        int currX = textMargin.getLeft() + textExtent.x + textMargin.getRight();
        if (currX < paddingLeft) { // for narrow text
            currX = paddingLeft;
        }
        int width = currX;
        boolean firstLine = true;

        final int count = brick.childrenCount();
        int lineStart = 0;

        for (;;) {
            // lineEnd counts position of line break
            int lineEnd;
            // PRIMARY LINE LOOP
            for (lineEnd = lineStart; lineEnd < count; lineEnd++) {
                final Brick child = brick.getChild(lineEnd);
                debug("i = %d, child = %s%n", lineEnd, child);
                child.doLayout(editor);
                child.x = currX;
                currX += child.getWidth() + spacing;
                lineAscent = Math.max(lineAscent, child.ascent);
                if (child instanceof LineBreak) {
                    lineEnd++;
                    break;
                }
            }
            debug("line #%d = (%d..%d)%n", lines.size(), lineStart, lineEnd);
            // END OF LINE
            int lineHeight;
            if (firstLine) {
                brick.textY = lineY + (lineAscent - textAscent);
                brick.ascent = lineY + lineAscent;
                lineHeight = brick.textY + textExtent.y + textMargin.getBottom();
                firstLine = false;
            } else {
                lineHeight = 0;
            }
            debug("line #%d height1 = %d, ascent1 = %d%n",
                    lines.size(), lineHeight, lineAscent);
            // SECONDARY LINE LOOP
            for (int j = lineStart; j < lineEnd; j++) {
                if (j >= count) {
                    debug("break on EOF (%d >= %d)%n", j, count);
                    break;
                }
                final Brick child = brick.getChild(j);
                debug("j = %d, child = %s%n", j, child);
                final int margin;
                if (child instanceof LineBreak) {
                    margin = 0;
                    if (lineHeight < 10) {
                        lineHeight = 10;
                    }
                    child.resize(child.getWidth(), lineHeight);
                } else {
                    margin = lineAscent - child.ascent;
                }
                child.y = lineY + margin;
                lineHeight = Math.max(lineHeight, child.getHeight() + margin);
            }
            debug("line #%d height2 = %d, ascent2 = %d%n",
                    lines.size(), lineHeight, lineAscent);
            width = Math.max(width, currX - spacing);

            debug("lineY = %d%n", lineY);
            final Line line =
                    brick.new Line(lineStart, lineEnd, lineY, lineHeight);
            lines.add(line);

            lineStart = lineEnd;
            lineY += lineHeight + lineSpacing;
            if (lineStart >= count) {
                break;
            }
            // init next line
            lineAscent = 0;
            currX = paddingLeft;
        }

        brick.setLines(lines);
        width += brickPadding.getRight();
        final int height = lineY - lineSpacing + brickPadding.getBottom();

        indent--;
        debug("END %d x %d -> %s%n", width, height, brick);
        return brick.resize(width, height);
    }
}
