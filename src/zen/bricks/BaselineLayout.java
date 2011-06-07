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
        final UI ui = editor.getUI();

        final StyleChain chain = ui.getStyleChain(brick, editor);
        final Margin textMargin = chain.get(TupleStyle.TEXT_MARGIN);
        final Margin brickPadding = chain.get(TupleStyle.PADDING);
        final int paddingLeft = brickPadding.getLeft();
        final int lineSpacing = chain.get(TupleStyle.LINE_SPACING);
        final int spacing = chain.get(TupleStyle.CHILDREN_SPACING);

        final ArrayList<Line> lines = new ArrayList<TupleBrick.Line>(1);

        final TupleStyle fontStyle = chain.find(TupleStyle.FONT);
        final Point textExtent =
                fontStyle.getTextExtent(brick.getText());

        brick.textX = textMargin.getLeft();
        brick.textExtent = textExtent;
        final int fontAscent = fontStyle.getFontAscent();
        int lineAscent = fontAscent;

        int lineY = brickPadding.getTop();
        int currX = textMargin.getLeft() + textExtent.x + textMargin.getRight();
        if (currX < paddingLeft) { // for narrow text
            currX = paddingLeft;
        }
        int width = currX;
        boolean firstLine = true;

        final int count = brick.getChildCount();
        int lineStart = 0;

        for (;;) {
            // lineEnd counts position of line break
            int lineEnd;
            // PRIMARY LINE LOOP
            for (lineEnd = lineStart; lineEnd < count; lineEnd++) {
                final Brick child = brick.getChild(lineEnd);
                child.doLayout(editor);
                child.x = currX;
                currX += child.getWidth() + spacing;
                lineAscent = Math.max(lineAscent, child.ascent);
                if (child instanceof LineBreak) {
                    lineEnd++;
                    break;
                }
            }
            // END OF LINE
            int lineHeight;
            if (firstLine) {
                brick.textY = lineY + (lineAscent - fontAscent);
                brick.ascent = lineY + lineAscent;
                lineHeight = brick.textY + textExtent.y + textMargin.getBottom();
                firstLine = false;
            } else {
                lineHeight = 0;
            }
            // SECONDARY LINE LOOP
            for (int j = lineStart; j < lineEnd; j++) {
                if (j >= count) {
                    break;
                }
                final Brick child = brick.getChild(j);
                final int margin;
                if (child instanceof LineBreak) {
                    margin = 0;
                    final int fontHeight = fontStyle.getFontHeight();
                    if (lineHeight < fontHeight) {
                        lineHeight = fontHeight;
                    }
                    child.resize(child.getWidth(), lineHeight);
                } else {
                    margin = lineAscent - child.ascent;
                }
                child.y = lineY + margin;
                lineHeight = Math.max(lineHeight, child.getHeight() + margin);
            }
            width = Math.max(width, currX - spacing);

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
        return brick.resize(width, height);
    }
}
