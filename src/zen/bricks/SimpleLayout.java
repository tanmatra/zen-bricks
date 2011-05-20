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
        final UI ui = editor.getUI();
        final StyleChain chain = ui.getStyleChain(brick, editor);
        final Margin textMargin = chain.getTextMargin();
        final Margin brickPadding = chain.getPadding();
        final int lineSpacing = chain.getLineSpacing();
        final int spacing = chain.getSpacing();

        brick.textX = textMargin.getLeft();
        brick.textY = textMargin.getTop();
        brick.textExtent = ui.getStyleChain(brick, editor).getTextExtent(brick.text);
        int width = textMargin.getLeft() + brick.textExtent.x;

        int currX = width + spacing;
        int currY = brickPadding.getTop();
        int currLineHeight = textMargin.getTop() + brick.textExtent.y;

        for (final TupleBrick.Line line : brick.getLines()) {
            for (final Brick child : line) {
                child.validate(editor);
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
