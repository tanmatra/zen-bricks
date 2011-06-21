package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class MultipartEditor implements IStyleEditor
{
    private StyleEditorPart<?>[] parts;

    private Composite composite;

    protected MultipartEditor() {
    }

    protected abstract StyleEditorPart<?>[] createParts();

    public void createControl(Composite parent) {
        parts = createParts();

        composite = new Composite(parent, SWT.NONE);

        int numColumns = 0;
        for (final StyleEditorPart<?> part : parts) {
            numColumns = Math.max(numColumns, part.getNumColumns());
        }

        GridLayoutFactory.fillDefaults().numColumns(numColumns).margins(5, 5)
                .applyTo(composite);

        for (int i = 0; i < parts.length; i++) {
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
