package zen.bricks.styleeditor;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import zen.bricks.ITextStyleEditor;
import zen.bricks.TupleStyle;

public class TextStyleEditor implements ITextStyleEditor
{
    // ================================================================== Fields

    final TupleStyle tupleStyle;

    private Composite composite;

    private final ArrayList<StyleEditorPart> parts = new ArrayList<StyleEditorPart>();

    // ============================================================ Constructors

    public TextStyleEditor(TupleStyle tupleStyle) {
        this.tupleStyle = tupleStyle;

        createParts();
    }

    // ================================================================= Methods

    private void addPart(StyleEditorPart part) {
        parts.add(part);
    }

    private void createParts() {
        addPart(new ColorEditorPart(tupleStyle.getForegroundColor(),
            "Foreground color")
        {
            protected void apply() {
                tupleStyle.setForegroundColor(getRGB());
            }
        });

        addPart(new ColorEditorPart(tupleStyle.getBackgroundColor(),
            "Background color", true)
        {
            protected void apply() {
                tupleStyle.setBackgroundColor(
                    isDefined(), getRGB(), isTransparent());
            }
        });

        addPart(new FontEditorPart(tupleStyle.getFontList(), "Font")
        {
            protected void apply() {
                tupleStyle.setFont(getFontList());
            }
        });
    }

    public void createControl(Composite parent) {
        composite = new Composite(parent, SWT.NONE);

        int numColumns = 0;
        for (final StyleEditorPart part : parts) {
            numColumns = Math.max(numColumns, part.getNumColumns());
        }

        GridLayoutFactory.fillDefaults().numColumns(numColumns).margins(5, 5)
            .applyTo(composite);

        for (final StyleEditorPart part : parts) {
            part.createWidgets(composite, numColumns);
        }
    }

    public Control getControl() {
        return composite;
    }

    public void apply() {
        for (final StyleEditorPart part : parts) {
            part.apply();
        }
    }

    public void cancel() {
        for (final StyleEditorPart part : parts) {
            part.cancel();
        }
    }
}
