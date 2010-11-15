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

    StyleChain parent;
    TupleStyle style;

    protected StyleChain(TupleStyle style, StyleChain parent) {
        this.style = style;
        this.parent = parent;
    }

    private TupleStyle findFont() {
        StyleChain chain = this;
        do {
            final TupleStyle st = chain.style;
            if (st.getFont() != null) {
                return st;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("Font not found in style chain");
    }

    private TupleStyle findForeground() {
        StyleChain chain = this;
        do {
            final TupleStyle st = chain.style;
            if (st.getForegroundColor() != null) {
                return st;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("No foreground found in style chain");
    }

    private TupleStyle findBackground() {
        StyleChain chain = this;
        do {
            final TupleStyle style = chain.style;
            if (style.isBackgroundDefined()) {
                return style;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("No background found in style chain");
    }

    TupleStyle findPadding() {
        StyleChain chain = this;
        do {
            final TupleStyle style = chain.style;
            if (style.padding != null) {
                return style;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("No padding found in style chain");
    }

    TupleStyle findTextMargin() {
        StyleChain chain = this;
        do {
            final TupleStyle style = chain.style;
            if (style.textMargin != null) {
                return style;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("No text margin found in style chain");
    }

    public Color getForegroundColor() {
        return findForeground().getForegroundColor();
    }

    public Color getBackgroundColor() {
        return findBackground().getBackgroundColor();
    }

    public Font getFont() {
        return findFont().getFont();
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
        return findPadding().getPadding();
    }

    public Margin getTextMargin() {
        return findTextMargin().getTextMargin();
    }
}
