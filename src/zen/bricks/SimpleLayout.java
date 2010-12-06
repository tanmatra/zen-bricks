package zen.bricks;

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

    public void doLayout(TupleBrick brick, Editor editor) {
        final UI ui = editor.getUI();
        final StyleChain chain = ui.getStyleChain(brick, editor);
        final Margin textMargin = chain.getTextMargin();
        final Margin brickPadding = chain.getPadding();
        final int lineSpacing = chain.getLineSpacing();
        final int spacing = chain.getSpacing();

        brick.textX = textMargin.getLeft();
        brick.textY = textMargin.getTop();
        brick.textExtent = ui.getStyleChain(brick, editor).getTextExtent(brick.text);
        brick.width = textMargin.getLeft() + brick.textExtent.x;

        int currX = brick.width + spacing;
        int currY = brickPadding.getTop();
        int currLineHeight = textMargin.getTop() + brick.textExtent.y;

        for (final TupleBrick.Line line : brick.getLines()) {
            for (final Brick child : line) {
                child.calculateSize(editor);
                child.x = currX;
                child.y = currY;
                currLineHeight = Math.max(currLineHeight, child.height);
                currX += child.width + spacing;
            }
            // line ended
            line.height = currLineHeight;
            line.y = currY;
            brick.width = Math.max(brick.width, currX - spacing);
            // prepare new line
            currX = brickPadding.getLeft();
            currY += currLineHeight + lineSpacing;
            currLineHeight = 0;
        }

        brick.width += brickPadding.getRight();
        brick.height = currY - lineSpacing + brickPadding.getBottom();
    }
}
