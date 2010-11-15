package zen.bricks.styleeditor;

import org.eclipse.swt.widgets.Composite;

abstract class SpacingEditorPart extends CheckedEditorPart
{
    private final Integer spacing;
    private LabelSpinnerPair pair;

    public SpacingEditorPart(String title, Integer spacing) {
        super(title);
        this.spacing = spacing;
    }

    protected void enabledCheckSelected(boolean selected) {
        pair.setEnabled(selected);
    }

    void createWidgets(Composite parent, int numColumns) {
        createEnabledCheck(parent);

        final Composite panel = createValuesPanel(parent, numColumns - 1);

        pair = new LabelSpinnerPair(panel, "Value:");
        if (spacing != null) {
            pair.setSelection(spacing);
            setEnabled(true);
        } else {
            pair.setEnabled(false);
        }
    }

    protected Integer getValue() {
        return isEnabled() ? pair.getSelection() : null;
    }
}
