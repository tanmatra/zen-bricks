package zen.bricks;

public class SimpleLayout extends TupleLayout
{
    public SimpleLayout(UI ui) {
        super(ui);
    }

    void doLayout(TextBrick brick) {
        brick.textY = ui.getTextMarginTop();
        brick.textExtent = ui.getTextExtent(brick.text);
        brick.width = ui.getTextMarginLeft() + brick.textExtent.x;

        final Margin brickPadding = ui.getBrickPadding();

        int currX = brick.width + ui.getSpacing();
        int currY = brickPadding.getTop();
        int currLineHeight = ui.getTextMarginTop() + brick.textExtent.y;

        final int count = brick.childrenCount();
        for (int i = 0; i < count; i++) {
            final Brick child = brick.getChild(i);
            child.calculateSize(ui);
            if (child.isLineBreak()) {
                currX = brickPadding.getLeft();
                currY += currLineHeight + ui.getLineSpacing();
                child.x = currX;
                child.y = currY;
                currLineHeight = child.height;
            } else {
                currLineHeight = Math.max(currLineHeight, child.height);
                currX += ui.getSpacing();
                // currY is unchanged
                child.x = currX;
                child.y = currY;
                currX += child.width;
            }
            brick.width = Math.max(brick.width, child.x + child.width);
        }

        brick.width += brickPadding.getRight();
        brick.height = currY + currLineHeight + brickPadding.getBottom();
    }
}
