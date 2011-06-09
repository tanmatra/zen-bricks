package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public abstract class StyleEditorPart<V>
        extends EditorPart<TupleStyle, V>
{
    private boolean mandatory;

    public StyleEditorPart(TupleStyle style, StyleProperty<V> property) {
        super(style, property);
        setMandatory(style.isTopLevel());
    }

    protected abstract int getNumColumns();

    public abstract void createWidgets(Composite parent, int numColumns);

    protected static GridDataFactory gridData() {
        return GridDataFactory.swtDefaults();
    }

    protected static GridDataFactory gridData(int span) {
        return gridData().span(span, 1);
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
