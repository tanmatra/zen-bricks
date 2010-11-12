package zen.bricks;

public class SimpleLayout extends TupleLayout
{
    public SimpleLayout(UI ui) {
        super(ui);
    }

    void doLayout(TupleBrick brick) {
        final StyleChain chain = ui.getStyleChain(brick);
        final Margin textMargin = ui.getTextMargin();
        final Margin brickPadding = chain.getPadding();

        brick.textY = textMargin.getTop();
        brick.textExtent = ui.getStyleChain(brick).getTextExtent(brick.text);
        brick.width = textMargin.getLeft() + brick.textExtent.x;

        int currX = brick.width + ui.getSpacing();
        int currY = brickPadding.getTop();
        int currLineHeight = textMargin.getTop() + brick.textExtent.y;

        for (final TupleBrick.Line line : brick.getLines()) {
            for (final Brick child : line) {
                child.calculateSize(ui);
                child.x = currX;
                child.y = currY;
                currLineHeight = Math.max(currLineHeight, child.height);
                currX += child.width + ui.getSpacing();
            }
            // line ended
            line.height = currLineHeight;
            line.y = currY;
            brick.width = Math.max(brick.width, currX - ui.getSpacing());
            // prepare new line
            currX = brickPadding.getLeft();
            currY += currLineHeight + ui.getLineSpacing();
            currLineHeight = 0;
        }

        brick.width += brickPadding.getRight();
        brick.height = currY - ui.getLineSpacing() + brickPadding.getBottom();
    }
}
