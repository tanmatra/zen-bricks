package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class SimpleLabelRenderer extends LabelRenderer
{
    // ============================================================ Class Fields

    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // ============================================================ Constructors

    public SimpleLabelRenderer(TupleBrick tupleBrick) {
        super(tupleBrick);
    }

    // ================================================================= Methods

    @Override
    public void doLayout(Editor editor) {
        final StyleChain chain = editor.getStyleChain(getTupleBrick());
        int flags = TEXT_FLAGS;
        if (chain.get(TupleStyle.TEXT_BACKGROUND).getColor() == null) {
            flags |= SWT.DRAW_TRANSPARENT;
        }

        final Margin margin = chain.get(TupleStyle.TEXT_PADDING);
        setTextX(margin.getLeft());
        setTextY(margin.getTop());

        final TupleStyle fontStyle = chain.find(TupleStyle.FONT);
        @SuppressWarnings("deprecation")
        final Point textExtent = fontStyle.getTextExtent(getText(), flags);
        setWidth(textExtent.x + margin.getHorizontalSum());
        setHeight(textExtent.y + margin.getVerticalSum());

        @SuppressWarnings("deprecation")
        final int fontAscent = fontStyle.getFontAscent();
        setAscent(fontAscent + margin.getTop());
    }

    @Override
    protected void doPaint(GC gc, int selfX, int selfY, Editor editor) {
        final StyleChain chain = editor.getStyleChain(getTupleBrick());
        gc.setFont(chain.find(TupleStyle.FONT).getFont());
        gc.setForeground(chain.find(TupleStyle.FOREGROUND).getForegroundColor());

        final TupleStyle backgroundStyle = chain.find(TupleStyle.TEXT_BACKGROUND);
        final Color color = backgroundStyle.getTextBackground().getColor();
        int flags = TEXT_FLAGS;
        if (color != null) {
            gc.setBackground(color);
        } else {
            flags |= SWT.DRAW_TRANSPARENT;
        }

        gc.drawText(getText(), selfX + getTextX(), selfY + getTextY(), flags);
    }

    @Override
    public void invalidate() {
    }
}
