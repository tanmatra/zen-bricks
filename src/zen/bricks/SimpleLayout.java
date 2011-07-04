package zen.bricks;

import org.eclipse.swt.graphics.Point;


public class SimpleLayout extends TupleLayout
{
    public SimpleLayout() {
    }

    public String getTitle() {
        return "Simple";
    }

    public String getName() {
        return "simple";
    }

    public boolean doLayout(TupleBrick brick, Editor editor) {
        final StyleChain chain = editor.getStyleChain(brick);
        final Margin textMargin = chain.get(TupleStyle.TEXT_MARGIN);
        final Margin brickPadding = chain.get(TupleStyle.PADDING);
        final int lineSpacing = chain.get(TupleStyle.LINE_SPACING);
        final int spacing = chain.get(TupleStyle.CHILDREN_SPACING);

        brick.setTextPosition(textMargin.getLeft(), textMargin.getTop());
        final Point textExtent = brick.applyTextStyle(chain);

        int width = textMargin.getLeft() + textExtent.x;

        int currX = width + spacing;
        int currY = brickPadding.getTop();
        int currLineHeight = textMargin.getTop() + textExtent.y;

        for (final TupleBrick.Line line : brick.getLines()) {
            for (final Brick child : line) {
                child.doLayout(editor);
                child.x = currX;
                child.y = currY;
                currLineHeight = Math.max(currLineHeight, child.getHeight());
                currX += child.getWidth() + spacing;
            }
            // line ended
            line.height = currLineHeight;
            line.y = currY;
            width = Math.max(width, currX - spacing);
            // prepare new line
            currX = brickPadding.getLeft();
            currY += currLineHeight + lineSpacing;
            currLineHeight = 0;
        }

        width += brickPadding.getRight();
        final int height = currY - lineSpacing + brickPadding.getBottom();
        return brick.resize(width, height);
    }
}
