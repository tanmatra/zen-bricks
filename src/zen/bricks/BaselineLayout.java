package zen.bricks;

import org.eclipse.swt.graphics.Point;

public class BaselineLayout extends TupleLayout
{
    public BaselineLayout(UI ui) {
        super(ui);
    }

    void doLayout(TextBrick brick) {
        final TextStyle textStyle = ui.getTextStyle(brick);
        final Point textExtent = textStyle.getTextExtent(brick.text);
        brick.textExtent = textExtent;
        final Margin brickPadding = ui.getBrickPadding();
        final int paddingLeft = brickPadding.getLeft();
        int width = paddingLeft;
        int bottom = brickPadding.getTop();

        int line = 0;
        final int textAscent = textStyle.getTextAscent();
        int lineAscent = textAscent;
        int lineWidth = ui.getTextMargin().getLeft() + textExtent.x;
        if (lineWidth < paddingLeft) { // for narrow text
            lineWidth = paddingLeft;
        }

        final int count = brick.childrenCount();

        int lineStart = 0;
        int lineEnd = 0;

        while (true) {
            // найти очередной конец строки или конец списка
            for (;;) {
                if (lineEnd == count) {
                    break;
                }
                final Brick child = brick.getChild(lineEnd);
                if (child.isLineBreak()) {
                    break;
                }
                lineEnd++;
            }

            // обработать строку
            if (line != 0) {
                bottom += ui.getLineSpacing();
            }
            for (int i = lineStart; i < lineEnd; i++) {
                final Brick child = brick.getChild(i);
//                if (i != lineStart) {
                    lineWidth += ui.getSpacing();
//                }
                child.calculateSize(ui);
                child.x = lineWidth;
                lineWidth += child.width;
                lineAscent = Math.max(lineAscent, child.ascent);
            }
            int lineHeight; // textExtent.y; // ???
            if (line == 0) {
                brick.textY = bottom + (lineAscent - textAscent); // ???
                lineHeight = brick.textY + textExtent.y;
            } else {
                lineHeight = 0;
            }
            for (int i = lineStart; i < lineEnd; i++) {
                final Brick child = brick.getChild(i);
                final int margin = lineAscent - child.ascent;
                child.y = bottom + margin;
                lineHeight = Math.max(lineHeight, margin + child.height);
            }

            // строка закончена
            if (line == 0) {
                brick.ascent = bottom + lineAscent;
            }
            bottom += lineHeight;
            width = Math.max(width, lineWidth);
            // можно ли двигаться дальше?
            if (lineEnd == count) {
                break;
            }
            // переход на следующую строку
            lineStart = lineEnd;
            lineEnd++;
            line++;
//            lineHeight = 0;
            lineWidth = paddingLeft;
            lineAscent = 0;
        }

        brick.width = width + brickPadding.getRight();
        brick.height = bottom + brickPadding.getBottom();
    }
}
