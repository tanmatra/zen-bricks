package zen.bricks;

import org.eclipse.swt.SWT;
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

    public <V> V get(StyleProperty<V> property) {
        return property.get(find(property));
    }

    private TupleStyle findFont() {
        return find(TupleStyle.FONT);
    }

    public Point getTextExtent(String text) {
        return findFont().savedGC.textExtent(text, TEXT_FLAGS);
    }

    public int getTextAscent() {
        final TupleStyle st = findFont();
        final FontMetrics fm = st.fontMetrics;
        return fm.getAscent() + fm.getLeading();
    }
}
