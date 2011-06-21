package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.Margin;
import zen.bricks.Property;
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.EditorPart;
import zen.bricks.swt.LabelSpinnerPair;

public abstract class MarginProperty<T> extends Property<T, Margin>
{
    // ============================================================ Constructors

    public MarginProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    public EditorPart<T, Margin> createEditorPart(T style) {
        return new MarginEditorPart<T>(style, this);
    }

    public void load(T object, Preferences preferences) {
        set(object, Margin.parseMargin(read(preferences)));
    }

    public void save(T object, Preferences preferences) {
        final Margin margin = get(object);
        write(preferences, (margin == null) ? null : margin.format());
    }

    // ========================================================== Nested Classes

    private static class MarginEditorPart<T> extends CheckedEditorPart<T, Margin>
    {
        private LabelSpinnerPair leftValue;
        private LabelSpinnerPair topValue;
        private LabelSpinnerPair rightValue;
        private LabelSpinnerPair bottomValue;

        public MarginEditorPart(T object, Property<T, Margin> property) {
            super(object, property);
        }

        protected void definedCheckChanged(boolean selected) {
            leftValue.setEnabled(selected);
            topValue.setEnabled(selected);
            rightValue.setEnabled(selected);
            bottomValue.setEnabled(selected);
        }

        public void createWidgets(Composite parent, int columns) {
            createDefinedCheck(parent);

            final Composite panel = createValuesPanel(parent, columns - 1, 8);

            leftValue = new LabelSpinnerPair(panel, "Left:");
            topValue = new LabelSpinnerPair(panel, "Top:");
            rightValue = new LabelSpinnerPair(panel, "Right:");
            bottomValue = new LabelSpinnerPair(panel, "Bottom:");

            final GridDataFactory indent =
                    GridDataFactory.swtDefaults().indent(10, 0);
            indent.applyTo(topValue.getLabel());
            indent.applyTo(rightValue.getLabel());
            indent.applyTo(bottomValue.getLabel());

            final Margin margin = getEditedValue();
            if (margin != null) {
                leftValue.setSelection(margin.getLeft());
                topValue.setSelection(margin.getTop());
                rightValue.setSelection(margin.getRight());
                bottomValue.setSelection(margin.getBottom());
            }

            setDefined(margin != null);
            definedCheckChanged(margin != null);
        }

        public Margin getValue() {
            if (!isDefined()) {
                return null;
            }
            return new Margin(
                    leftValue.getSelection(),
                    topValue.getSelection(),
                    rightValue.getSelection(),
                    bottomValue.getSelection());
        }
    }
}
