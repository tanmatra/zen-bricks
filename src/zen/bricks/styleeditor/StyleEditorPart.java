package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public abstract class StyleEditorPart<T>
{
    protected final StyleProperty<T> property;

    private final TupleStyle style;

    public StyleEditorPart(StyleProperty<T> property, TupleStyle style) {
        this.property = property;
        this.style = style;
    }

    abstract int getNumColumns();

    abstract void createWidgets(Composite parent, int numColumns);

    public abstract T getValue();

    public void apply() {
        property.apply(this, style);
    }

    public void cancel() {
    }

    protected static GridDataFactory gridData() {
        return GridDataFactory.swtDefaults();
    }

    protected static GridDataFactory gridData(int span) {
        return gridData().span(span, 1);
    }
}
