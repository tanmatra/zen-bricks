package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public class PropertiesListEditor implements IStyleEditor
{
    private final StyleProperty<?>[] properties;

    private final TupleStyle style;

    private StyleEditorPart<?>[] parts;

    private Composite composite;

    public PropertiesListEditor(StyleProperty<?>[] properties,
                                TupleStyle style)
    {
        this.properties = properties;
        this.style = style;
    }

    public void createControl(Composite parent) {
        final int count = properties.length;
        parts = new StyleEditorPart<?>[count];
        for (int i = 0; i < count; i++) {
            parts[i] = properties[i].createEditorPart(style);
        }

        composite = new Composite(parent, SWT.NONE);

        int numColumns = 0;
        for (final StyleEditorPart<?> part : parts) {
            numColumns = Math.max(numColumns, part.getNumColumns());
        }

        GridLayoutFactory.fillDefaults().numColumns(numColumns).margins(5, 5)
                .applyTo(composite);

        for (int i = 0; i < count; i++) {
            parts[i].createWidgets(composite, numColumns);
        }
    }

    public Control getControl() {
        return composite;
    }

    public void apply() {
        for (final StyleEditorPart<?> part : parts) {
            part.apply();
        }
    }

    public void cancel() {
        for (final StyleEditorPart<?> part : parts) {
            part.cancel();
        }
    }
}
