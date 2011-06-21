package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.ColorUtil;
import zen.bricks.Property;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.EditorPart;

public class TextBackgroundProperty extends Property<TupleStyle, TransparentColor>
{
    // ============================================================ Class Fields

    private static final String TRANSPARENT = "transparent";

    // ============================================================ Constructors

    public TextBackgroundProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    public EditorPart<TupleStyle, TransparentColor> createEditorPart(
            TupleStyle object)
    {
        return new TransparentColorEditorPart(object, this);
    }

    public TransparentColor get(TupleStyle style) {
        return style.getTextBackground();
    }

    public void set(TupleStyle style, TransparentColor value) {
        style.setTextBackground(value);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String string = read(preferences);
        final TransparentColor transparentColor;
        if (string == null) {
            transparentColor = null;
        } else if (TRANSPARENT.equals(string)) {
            transparentColor = new TransparentColor();
        } else {
            final Device device = style.getUI().getDevice();
            final RGB rgb = ColorUtil.parse(device, string);
            transparentColor = new TransparentColor(new Color(device, rgb));
        }
        set(style, transparentColor);
    }

    public void save(TupleStyle object, Preferences preferences) {
        final TransparentColor transparentColor = get(object);
        if (transparentColor == null) {
            write(preferences, null);
            return;
        }
        final Color color = transparentColor.getColor();
        if (color == null) {
            write(preferences, TRANSPARENT);
        } else {
            write(preferences, ColorUtil.format(color));
        }
    }

    // ========================================================== Nested Classes

    private static class TransparentColorEditorPart
            extends CheckedEditorPart<TupleStyle, TransparentColor>
    {
        private ColorSelector colorSelector;
        private Button transparentCheck;

        public TransparentColorEditorPart(TupleStyle object,
                Property<TupleStyle, TransparentColor> property)
        {
            super(object, property);
        }

        public void createWidgets(Composite parent, int columns) {
            createDefinedCheck(parent);

            final Composite panel = createValuesPanel(parent, columns - 1);

            colorSelector = new ColorSelector(panel);

            transparentCheck = new Button(panel, SWT.CHECK);
            transparentCheck.setText("Transparent");
            transparentCheck.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    transparentCheckChanged();
                }
            });

            final TransparentColor transparentColor = getEditedValue();
            if (transparentColor != null) {
                setDefined(true);
                final Color color = transparentColor.getColor();
                if (color == null) {
                    transparentCheck.setSelection(true);
                } else {
                    transparentCheck.setSelection(false);
                    colorSelector.setColorValue(color.getRGB());
                }
            } else {
                setDefined(false);
            }
            definedCheckChanged(isDefined());
        }

        @Override
        protected void definedCheckChanged(boolean defined) {
            transparentCheck.setEnabled(defined);
            transparentCheckChanged();
        }

        void transparentCheckChanged() {
            final boolean enabled =
                    isDefined() && !transparentCheck.getSelection();
            colorSelector.setEnabled(enabled);
        }

        public TransparentColor getValue() {
            if (!isDefined()) {
                return null;
            } else if (transparentCheck.getSelection()) {
                return new TransparentColor();
            } else {
                final RGB rgb = colorSelector.getColorValue();
                final Color color;
                if (rgb == null) {
                    color = null;
                } else {
                    final Device device = getEditedObject().getUI().getDevice();
                    color = new Color(device, rgb);
                }
                return new TransparentColor(color);
            }
        }
    }
}
