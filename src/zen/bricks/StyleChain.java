package zen.bricks;

public class StyleChain
{
    private final StyleChain parent;

    private final TupleStyle style;

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
                "Style property %s not found in chain", property));
    }

    public <V> V get(StyleProperty<V> property) {
        return property.get(find(property));
    }
}
