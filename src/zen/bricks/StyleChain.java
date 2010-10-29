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
    TextStyle style;

    public StyleChain(TextStyle style, StyleChain parent) {
        this.style = style;
        this.parent = parent;
    }

    private TextStyle findFont() {
        StyleChain chain = this;
        do {
            final TextStyle st = chain.style;
            if (st.font != null) {
                return st;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("Font not found in style chain");
    }

    private TextStyle findForeground() {
        StyleChain chain = this;
        do {
            final TextStyle st = chain.style;
            if (st.foregroundColor != null) {
                return st;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("No foreground found in style chain");
    }

    private TextStyle findBackground() {
        StyleChain chain = this;
        do {
            final TextStyle style = chain.style;
            if (style.transparent != null) {
                return style;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("No background found in style chain");
    }

    public Color getForegroundColor() {
        return findForeground().foregroundColor;
    }

    public Color getBackgroundColor() {
        return findBackground().backgroundColor;
    }

    public Font getFont() {
        return findFont().font;
    }

    public Point getTextExtent(String text) {
        return findFont().savedGC.textExtent(text, TEXT_FLAGS);
    }

    public void paintText(GC gc, int x, int y, String text) {
        gc.setFont(getFont());
        gc.setForeground(getForegroundColor());

        final TextStyle background = findBackground();
        int flags = TEXT_FLAGS;
        if (background.transparent) {
            flags |= SWT.DRAW_TRANSPARENT;
        } else {
            final Color backgroundColor = background.backgroundColor;
            if (backgroundColor != null) {
                gc.setBackground(backgroundColor);
            }
        }

        gc.drawText(text, x, y, flags);
    }

    public int getTextAscent() {
        final TextStyle st = findFont();
        final FontMetrics fm = st.fontMetrics;
        return fm.getAscent() + fm.getLeading();
    }
}
