package zen.bricks.styleeditor;

import org.eclipse.swt.widgets.Composite;

abstract class StyleEditorPart
{
    abstract int getNumColumns();

    abstract void createWidgets(Composite parent, int numColumns);

    abstract void apply();

    abstract void cancel();
}
