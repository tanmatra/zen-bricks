package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class MultipartEditor implements IStyleEditor
{
    private EditorPart<?, ?>[] parts;

    private Composite composite;

    protected MultipartEditor() {
    }

    protected abstract EditorPart<?, ?>[] createParts();

    @Override
    public Control createControl(Composite parent) {
        parts = createParts();

        composite = new Composite(parent, SWT.NONE);

        int numColumns = 0;
        for (final EditorPart<?, ?> part : parts) {
            numColumns = Math.max(numColumns, part.getNumColumns());
        }

        GridLayoutFactory.fillDefaults().numColumns(numColumns).margins(5, 5).applyTo(composite);

        for (int i = 0; i < parts.length; i++) {
            parts[i].createWidgets(composite, numColumns);
        }

        return composite;
    }

    @Override
    public void apply() {
        for (final EditorPart<?, ?> part : parts) {
            part.apply();
        }
    }

    @Override
    public void cancel() {
        for (final EditorPart<?, ?> part : parts) {
            part.cancel();
        }
    }
}
