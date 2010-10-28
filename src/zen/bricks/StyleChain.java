package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
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

    public Font getFont() {
        StyleChain chain = this;
        do {
            final Font font = chain.style.font;
            if (font != null) {
                return font;
            }
            chain = chain.parent;
        } while (chain != null);
        return null;

    }

    public Color getForegroundColor() {
        StyleChain chain = this;
        do {
            final Color color = chain.style.foregroundColor;
            if (color != null) {
                return color;
            }
            chain = chain.parent;
        } while (chain != null);
        return null;
    }

    public Color getBackgroundColor() {
        StyleChain chain = this;
        do {
            final Color color = chain.style.backgroundColor;
            if (color != null) {
                return color;
            }
            chain = chain.parent;
        } while (chain != null);
        return null;
    }

    public Point getTextExtent(String text) {
        StyleChain chain = this;
        do {
            final TextStyle st = chain.style;
            if (st.font != null) {
                return st.savedGC.textExtent(text, TEXT_FLAGS);
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("No font found in style chain");
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
        // TODO Auto-generated method stub
        return 0;
    }
}
