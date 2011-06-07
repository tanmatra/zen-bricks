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

    public TupleStyle find(StyleProperty<?> property) {
        StyleChain chain = this;
        do {
            final TupleStyle style = chain.style;
            if (property.isDefined(style)) {
                return style;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error(String.format(
                "Style property \"%s\" (%s) not found in chain",
                property.getTitle(), property.getKey()));
    }

    private TupleStyle findFont() {
        return find(TupleStyle.FONT);
    }

    public TupleStyle findTextBackground() {
        return find(TupleStyle.TEXT_BACKGROUND);
    }

    public Color getForegroundColor() {
        return find(TupleStyle.FOREGROUND).getForegroundColor();
    }

    public Color getBackgroundColor() {
        return find(TupleStyle.BACKGROUND).getBackgroundColor();
    }

    public Font getFont() {
        return find(TupleStyle.FONT).getFont();
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
        return find(TupleStyle.PADDING).getPadding();
    }

    public Margin getTextMargin() {
        return find(TupleStyle.TEXT_MARGIN).getTextMargin();
    }

    public int getLineSpacing() {
        return find(TupleStyle.LINE_SPACING).getLineSpacing();
    }

    public int getSpacing() {
        return find(TupleStyle.CHILDREN_SPACING).getSpacing();
    }
}
