package zen.bricks;

import org.eclipse.swt.graphics.Point;

public class BaselineLayout extends TupleLayout
{
    public BaselineLayout(UI ui) {
        super(ui);
    }

    void doLayout(TupleBrick brick) {
        final StyleChain chain = ui.getStyleChain(brick);
        final Margin textMargin = ui.getTextMargin();
        final Margin brickPadding = chain.getPadding();
        final int paddingLeft = brickPadding.getLeft();
        final int lineSpacing = ui.getLineSpacing();

        final Point textExtent = chain.getTextExtent(brick.text);
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
        for (final TupleBrick.Line line : brick.getLines()) {
            for (final Brick child : line) {
                child.calculateSize(ui);
                child.x = currX;
                currX += child.width + ui.getSpacing();
                lineAscent = Math.max(lineAscent, child.ascent);
            }
            int lineHeight;
            if (firstLine) {
                brick.textY = lineY + (lineAscent - textAscent);
                brick.ascent = lineY + lineAscent;
                lineHeight = brick.textY + textExtent.y + textMargin.getBottom();
                firstLine = false;
            } else {
                lineHeight = 0;
            }
            for (final Brick child : line) {
                final int margin = lineAscent - child.ascent;
                child.y = lineY + margin;
                lineHeight = Math.max(lineHeight, child.height + margin);
            }
            width = Math.max(width, currX - ui.getSpacing());
            lineY += lineHeight + lineSpacing;
            // next line
            currX = paddingLeft;
            lineAscent = 0;
        }

        brick.width = width + brickPadding.getRight();
        brick.height = lineY - lineSpacing + brickPadding.getBottom();
    }
}
