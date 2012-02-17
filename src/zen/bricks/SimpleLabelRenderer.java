package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class SimpleLabelRenderer extends LabelRenderer
{
    // ============================================================ Class Fields

    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // ============================================================ Constructors

    public SimpleLabelRenderer(TupleBrick tupleBrick) {
        super(tupleBrick);
    }

    // ================================================================= Methods

    public void doLayout(Editor editor) {
        final StyleChain chain = editor.getStyleChain(getTupleBrick());
        int flags = TEXT_FLAGS;
        if (chain.get(TupleStyle.TEXT_BACKGROUND).getColor() == null) {
            flags |= SWT.DRAW_TRANSPARENT;
        }
        final TupleStyle fontStyle = chain.find(TupleStyle.FONT);
        textExtent = fontStyle.getTextExtent(getText(), flags);
    }

    public void paint(GC gc, int baseX, int baseY, Rectangle clipping,
            Editor editor)
    {
        final int textX = baseX + getX();
        final int textY = baseY + getY();
        if (!clipping.intersects(textX, textY, textExtent.x, textExtent.y)) {
            return;
        }

        final StyleChain chain = editor.getStyleChain(getTupleBrick());
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

        gc.drawText(getText(), textX, textY, flags);
    }

    public void invalidate() {
    }
}
