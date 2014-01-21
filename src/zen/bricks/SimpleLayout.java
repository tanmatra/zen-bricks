package zen.bricks;

public class SimpleLayout extends TupleLayout
{
    public SimpleLayout() {
    }

    @Override
    public String getTitle() {
        return "Simple";
    }

    @Override
    public String getName() {
        return "simple";
    }

    @Override
    public boolean doLayout(TupleBrick brick, Editor editor) {
        final LabelRenderer labelRenderer = brick.getLabelRenderer();
        final StyleChain chain = editor.getStyleChain(brick);
        final Margin brickPadding = chain.get(TupleStyle.PADDING);
        final int lineSpacing = chain.get(TupleStyle.LINE_SPACING);
        final int spacing = chain.get(TupleStyle.CHILDREN_SPACING);

        labelRenderer.doLayout(editor);
        labelRenderer.setX(brickPadding.getLeft());
        labelRenderer.setY(brickPadding.getTop());

        int width = labelRenderer.getWidth();

        int currX = width + spacing;
        int currY = brickPadding.getTop();
        int currLineHeight = labelRenderer.getHeight();

        for (final TupleBrick.Line line : brick.getLines()) {
            for (final Brick child : line) {
                child.doLayout(editor);
                child.setX(currX);
                child.setY(currY);
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
