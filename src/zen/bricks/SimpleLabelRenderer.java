package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class SimpleLabelRenderer extends LabelRenderer
{
    // ============================================================ Class Fields

    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // ================================================================= Methods

    public Point calculateSize(TupleBrick tuple, StyleChain chain) {
        int flags = TEXT_FLAGS;
        if (chain.get(TupleStyle.TEXT_BACKGROUND).getColor() == null) {
            flags |= SWT.DRAW_TRANSPARENT;
        }
        final TupleStyle fontStyle = chain.find(TupleStyle.FONT);
        textExtent = fontStyle.getTextExtent(tuple.getText(), flags);
        return textExtent;
    }

    public void paint(TupleBrick tuple, GC gc, int baseX, int baseY,
            Rectangle clipping, StyleChain chain)
    {
        final int textX = baseX + textPosition.x;
        final int textY = baseY + textPosition.y;
        if (!clipping.intersects(textX, textY, textExtent.x, textExtent.y)) {
            return;
        }

        gc.setFont(chain.find(TupleStyle.FONT).getFont());
        gc.setForeground(
                chain.find(TupleStyle.FOREGROUND).getForegroundColor());

        final TupleStyle backgroundStyle =
                chain.find(TupleStyle.TEXT_BACKGROUND);
        final Color color = backgroundStyle.getTextBackground().getColor();
        int flags = TEXT_FLAGS;
        if (color != null) {
            gc.setBackground(color);
        } else {
            flags |= SWT.DRAW_TRANSPARENT;
        }

        gc.drawText(tuple.getText(), textX, textY, flags);
    }
}
