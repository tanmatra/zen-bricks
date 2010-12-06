package zen.bricks.styleeditor;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.parts.ColorEditorPart;
import zen.bricks.styleeditor.parts.FontEditorPart;
import zen.bricks.styleeditor.parts.IntegerEditorPart;
import zen.bricks.styleeditor.parts.MarginEditorPart;

/*
 * Currently unused. Can be deleted.
 */
public class TupleStyleEditor implements IBrickStyleEditor
{
    // ================================================================== Fields

    final TupleStyle tupleStyle;

    private Composite composite;

    private final ArrayList<StyleEditorPart<?>> parts =
            new ArrayList<StyleEditorPart<?>>();

    // ============================================================ Constructors

    public TupleStyleEditor(TupleStyle tupleStyle) {
        this.tupleStyle = tupleStyle;

        createParts();
    }

    // ================================================================= Methods

    private void addPart(StyleEditorPart<?> part) {
        parts.add(part);
    }

    private void createParts() {
        addPart(new ColorEditorPart(TupleStyle.FOREGROUND, tupleStyle));
        addPart(new ColorEditorPart(TupleStyle.BACKGROUND, tupleStyle));
        addPart(new ColorEditorPart(TupleStyle.TEXT_BACKGROUND, tupleStyle,
                tupleStyle.getTextBackground()));
        addPart(new FontEditorPart(TupleStyle.FONT, tupleStyle));
        addPart(new MarginEditorPart(TupleStyle.PADDING, tupleStyle));
        addPart(new MarginEditorPart(TupleStyle.TEXT_MARGIN, tupleStyle));
        addPart(new IntegerEditorPart(TupleStyle.LINE_SPACING, tupleStyle));
        addPart(new IntegerEditorPart(TupleStyle.CHILDREN_SPACING, tupleStyle));
        // addPart(new LayoutEditorPart(TupleStyle.LAYOUT, tupleStyle));
    }

    public void createControl(Composite parent) {
        composite = new Composite(parent, SWT.NONE);

        int numColumns = 0;
        for (final StyleEditorPart<?> part : parts) {
            numColumns = Math.max(numColumns, part.getNumColumns());
        }

        GridLayoutFactory.fillDefaults().numColumns(numColumns).margins(5, 5)
            .applyTo(composite);

        for (final StyleEditorPart<?> part : parts) {
            part.createWidgets(composite, numColumns);
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
