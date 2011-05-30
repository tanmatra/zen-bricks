package zen.bricks;

import org.eclipse.swt.graphics.Point;

public class BaselineLayout extends TupleLayout
{
    public BaselineLayout() {
    }

    public String getTitle() {
        return "Base line";
    }

    public String getName() {
        return "baseline";
    }

    public boolean doLayout(TupleBrick brick, Editor editor) {
        final UI ui = editor.getUI();
        final StyleChain chain = ui.getStyleChain(brick, editor);
        final Margin textMargin = chain.getTextMargin();
        final Margin brickPadding = chain.getPadding();
        final int paddingLeft = brickPadding.getLeft();
        final int lineSpacing = chain.getLineSpacing();
        final int spacing = chain.getSpacing();

        final Point textExtent = chain.getTextExtent(brick.text);
        brick.textX = textMargin.getLeft();
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
                child.doLayout(editor);
                child.x = currX;
                currX += child.getWidth() + spacing;
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
                lineHeight = Math.max(lineHeight, child.getHeight() + margin);
            }
            line.y = lineY;
            line.height = lineHeight;
            width = Math.max(width, currX - spacing);
            lineY += lineHeight + lineSpacing;
            // next line
            currX = paddingLeft;
            lineAscent = 0;
        }

        width += brickPadding.getRight();
        final int height = lineY - lineSpacing + brickPadding.getBottom();
        return brick.resize(width, height);
    }
}
