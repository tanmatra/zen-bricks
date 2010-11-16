package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class StyleChain
{
    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    final StyleChain parent;
    final TupleStyle style;

    protected StyleChain(TupleStyle style, StyleChain parent) {
        this.style = style;
        this.parent = parent;
    }

    private TupleStyle findFont() {
        return TupleStyle.FONT.find(this);
    }

    private TupleStyle findBackground() {
        return TupleStyle.BACKGROUND.find(this);
    }

    public Color getForegroundColor() {
        return TupleStyle.FOREGROUND.find(this).getForegroundColor();
    }

    public Color getBackgroundColor() {
        return findBackground().getBackgroundColor();
    }

    public Font getFont() {
        return TupleStyle.FONT.find(this).getFont();
    }

    public Point getTextExtent(String text) {
        return findFont().savedGC.textExtent(text, TEXT_FLAGS);
    }

    public void paintText(GC gc, int x, int y, String text) {
        gc.setFont(getFont());
        gc.setForeground(getForegroundColor());

        final TupleStyle background = findBackground();
        int flags = TEXT_FLAGS;
        if (background.transparent) {
            flags |= SWT.DRAW_TRANSPARENT;
        } else {
            final Color backgroundColor = background.getBackgroundColor();
            if (backgroundColor != null) {
                gc.setBackground(backgroundColor);
            }
        }

        gc.drawText(text, x, y, flags);
    }

    public int getTextAscent() {
        final TupleStyle st = findFont();
        final FontMetrics fm = st.fontMetrics;
        return fm.getAscent() + fm.getLeading();
    }

    public Margin getPadding() {
        return TupleStyle.PADDING.find(this).getPadding();
    }

    public Margin getTextMargin() {
        return TupleStyle.TEXT_MARGIN.find(this).getTextMargin();
    }

    public int getLineSpacing() {
        return TupleStyle.LINE_SPACING.find(this).getLineSpacing();
    }

    public int getSpacing() {
        return TupleStyle.CHILD_SPACING.find(this).getSpacing();
    }
}
