package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Composite;

abstract class StyleEditorPart
{
    abstract int getNumColumns();

    abstract void createWidgets(Composite parent, int numColumns);

    abstract void apply();

    abstract void cancel();

    protected static GridDataFactory gridData() {
        return GridDataFactory.fillDefaults();
    }

    protected static GridDataFactory gridData(int span) {
        return gridData().span(span, 1);
    }
}
