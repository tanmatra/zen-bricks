package zen.bricks;

public class SimpleLayout extends TupleLayout
{
    SimpleLayout(UI ui) {
        super(ui);
    }

    void doLayout(TextBrick brick) {
        brick.textExtent = ui.getTextExtent(brick.text);
        brick.width = ui.getTextMarginLeft() + brick.textExtent.x;
        int currX = brick.width + ui.getSpacing();
        int currY = ui.getBrickPaddingTop();
        int currLineHeight = ui.getTextMarginTop() + brick.textExtent.y;
        for (final Brick child : brick.children) {
            child.calculateSize(ui);
            if (child.isLineBreak()) {
                currX = ui.getBrickPaddingLeft();
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
        brick.width += ui.getBrickPaddingRight();
        brick.height = currY + currLineHeight + ui.getBrickPaddingBottom();
    }
}
