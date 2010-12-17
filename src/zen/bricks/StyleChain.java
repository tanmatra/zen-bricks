package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;

public class StyleChain
{
    static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    final StyleChain parent;
    final TupleStyle style;

    protected StyleChain(TupleStyle style, StyleChain parent) {
        if (style == null) {
            throw new IllegalArgumentException("null style for chain");
        }
        this.style = style;
        this.parent = parent;
    }

    private TupleStyle findFont() {
        return TupleStyle.FONT.find(this);
    }

    public TupleStyle findTextBackground() {
        return TupleStyle.TEXT_BACKGROUND.find(this);
    }

    public Color getForegroundColor() {
        return TupleStyle.FOREGROUND.find(this).getForegroundColor();
    }

    public Color getBackgroundColor() {
        return TupleStyle.BACKGROUND.find(this).getBackgroundColor();
    }

    public Font getFont() {
        return TupleStyle.FONT.find(this).getFont();
    }

    public Point getTextExtent(String text) {
        return findFont().savedGC.textExtent(text, TEXT_FLAGS);
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
        return TupleStyle.CHILDREN_SPACING.find(this).getSpacing();
    }
}
