package zen.bricks;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;

import zen.bricks.TupleBrick.Line;

public class BaselineLayout extends TupleLayout
{
    public BaselineLayout() {
    }

    public String getTitle() {
        return "Base line";
    }

    public String getName() {
        return "baseline";
    }

    public boolean doLayout(TupleBrick brick, Editor editor) {
        final StyleChain chain = editor.getStyleChain(brick);
        final Margin textMargin = chain.get(TupleStyle.TEXT_MARGIN);
        final Margin brickPadding = chain.get(TupleStyle.PADDING);
        final int paddingLeft = brickPadding.getLeft();
        final int lineSpacing = chain.get(TupleStyle.LINE_SPACING);
        final int spacing = chain.get(TupleStyle.CHILDREN_SPACING);

        final ArrayList<Line> lines = new ArrayList<Line>(1);

        final TupleStyle fontStyle = chain.find(TupleStyle.FONT);
        final int fontHeight = fontStyle.getFontHeight();
        final Point textExtent = brick.applyTextStyle(chain);

        final int textAscent = textMargin.getTop() + fontStyle.getFontAscent();
        int lineAscent = textAscent;

        int lineY = brickPadding.getTop();
        int currX = textMargin.getLeft() + textExtent.x + textMargin.getRight();
        if (currX < paddingLeft) { // for narrow text
            currX = paddingLeft;
        }
        int lineX = currX;
        int width = currX;
        boolean firstLine = true;

        final int count = brick.getChildCount();
        int lineStart = 0;

        for (;;) {
            boolean breakFound = false;
            // lineEnd counts position of line break
            int lineEnd;
            // PRIMARY LINE LOOP
            for (lineEnd = lineStart; lineEnd < count; lineEnd++) {
                final Brick child = brick.getChild(lineEnd);
                child.doLayout(editor);
                child.x = currX;
                currX += child.getWidth() + spacing;
                lineAscent = Math.max(lineAscent, child.getAscent());
                if (child instanceof LineBreak) {
                    breakFound = true;
                    lineEnd++;
                    break;
                }
            }
            // END OF LINE
            int lineHeight;
            if (firstLine) {
                final int textY =
                        lineY + textMargin.getTop() + (lineAscent - textAscent);
                brick.setTextPosition(textMargin.getLeft(), textY);
                brick.setAscent(lineY + lineAscent);
                lineHeight = textY + textExtent.y + textMargin.getBottom();
                firstLine = false;
            } else {
                lineHeight = fontHeight;
            }

            // SECONDARY LINE LOOP
            for (int j = lineStart; j < lineEnd; j++) {
                final Brick child = brick.getChild(j);
                final int margin;
                if (child instanceof LineBreak) {
                    margin = 0;
                    child.resize(child.getWidth(), lineHeight);
                } else {
                    margin = lineAscent - child.getAscent();
                }
                child.y = lineY + margin;
                lineHeight = Math.max(lineHeight, child.getHeight() + margin);
            }
            width = Math.max(width, currX - spacing);

            final Line line =
                    brick.new Line(lineStart, lineEnd, lineY, lineHeight);
            line.x = lineX;
            lines.add(line);

            lineY += lineHeight + lineSpacing;

            if (!breakFound) {
                break;
            }
            lineStart = lineEnd;

            // init next line
            lineAscent = 0;
            currX = paddingLeft;
            lineX = currX;
        }

        brick.setLines(lines);
        width += brickPadding.getRight();
        final int height = lineY - lineSpacing + brickPadding.getBottom();
        return brick.resize(width, height);
    }
}
