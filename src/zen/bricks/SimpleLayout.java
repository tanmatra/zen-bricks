package zen.bricks;

public class SimpleLayout extends TupleLayout
{
    public SimpleLayout(UI ui) {
        super(ui);
    }

    void doLayout(TextBrick brick) {
        final Margin textMargin = ui.getTextMargin();
        final Margin brickPadding = ui.getBrickPadding();

        brick.textY = textMargin.getTop();
        brick.textExtent = ui.getTextStyle(brick).getTextExtent(brick.text);
        brick.width = textMargin.getLeft() + brick.textExtent.x;

        int currX = brick.width + ui.getSpacing();
        int currY = brickPadding.getTop();
        int currLineHeight = textMargin.getTop() + brick.textExtent.y;

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
