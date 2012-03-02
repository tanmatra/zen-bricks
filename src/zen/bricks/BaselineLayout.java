package zen.bricks;

import java.util.List;

import zen.bricks.TupleBrick.Line;

public class BaselineLayout extends TupleLayout
{
    private static final int MINIMUM_LINE_HEIGHT = 16;

    public BaselineLayout() {
    }

    public String getTitle() {
        return "Base line";
    }

    public String getName() {
        return "baseline";
    }

    public boolean doLayout(TupleBrick tupleBrick, Editor editor) {
        final StyleChain chain = editor.getStyleChain(tupleBrick);
        final Margin padding = chain.get(TupleStyle.PADDING);
        final int indent = chain.get(TupleStyle.INDENT);
        final int lineSpacing = chain.get(TupleStyle.LINE_SPACING);
        final int spacing = chain.get(TupleStyle.CHILDREN_SPACING);

        final LabelRenderer labelRenderer = tupleBrick.getLabelRenderer();
        labelRenderer.doLayout(editor);

        int lineY = padding.getTop();

        int width = 0;

        final List<Line> lines = tupleBrick.layoutLines();
        final int lineCount = lines.size();
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            int currX;
            int lineAscent;
            if (lineIndex == 0) {
                currX = padding.getLeft();
                labelRenderer.setX(currX);
                currX += labelRenderer.getWidth();
                lineAscent = labelRenderer.getAscent();
            } else {
                currX = indent;
                lineAscent = 0;
            }

            final Line line = lines.get(lineIndex);
            line.setX(currX);
            final List<Brick> children = line.getChildren();
            final int childCount = children.size();

            // first loop
            for (int i = 0; i < childCount; i++) {
                if (i == 0) {
                    if (lineIndex == 0) {
                        currX = Math.max(indent, currX + spacing);
                    }
                } else {
                    currX += spacing;
                }
                final Brick child = children.get(i);
                child.setX(currX);
                child.doLayout(editor);
                currX += child.getWidth();
                lineAscent = Math.max(lineAscent, child.getAscent());
            }

            int lineHeight;
            if (lineIndex == 0) {
                tupleBrick.setAscent(lineY + lineAscent);
                final int margin = lineAscent - labelRenderer.getAscent();
                labelRenderer.setY(lineY + margin);
                lineHeight = margin + labelRenderer.getHeight();
            } else {
                lineHeight = MINIMUM_LINE_HEIGHT;
            }

            // second loop
            for (int i = 0; i < childCount; i++) {
                final Brick child = children.get(i);
                final int margin;
                if (child instanceof LineBreak) {
                    margin = 0;
                    child.resize(child.getWidth(), lineHeight);
                } else {
                    margin = lineAscent - child.getAscent();
                }
                child.setY(lineY + margin);
                lineHeight = Math.max(lineHeight, child.getHeight() + margin);
            }
            line.setY(lineY);
            line.setHeight(lineHeight);
            lineY += lineHeight + lineSpacing;
            width = Math.max(width, currX);
        }

        width += padding.getRight();
        final int height = lineY - lineSpacing + padding.getBottom();
        return tupleBrick.resize(width, height);
    }
}
