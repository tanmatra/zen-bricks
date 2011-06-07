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

    public boolean doLayout(TupleBrick brick, Editor editor) {
        final StyleChain chain = editor.getUI().getStyleChain(brick, editor);
        final Margin textMargin = chain.get(TupleStyle.TEXT_MARGIN);
        final Margin brickPadding = chain.get(TupleStyle.PADDING);
        final int lineSpacing = chain.get(TupleStyle.LINE_SPACING);
        final int spacing = chain.get(TupleStyle.CHILDREN_SPACING);

        brick.textX = textMargin.getLeft();
        brick.textY = textMargin.getTop();
        brick.textExtent =
                chain.find(TupleStyle.FONT).getTextExtent(brick.getText());

        int width = textMargin.getLeft() + brick.textExtent.x;

        int currX = width + spacing;
        int currY = brickPadding.getTop();
        int currLineHeight = textMargin.getTop() + brick.textExtent.y;

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
