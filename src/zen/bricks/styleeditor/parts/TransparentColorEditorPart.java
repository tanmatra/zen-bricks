package zen.bricks.styleeditor.parts;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.properties.TransparentColor;

public class TransparentColorEditorPart
        extends CheckedEditorPart<TransparentColor>
{
    private ColorSelector colorSelector;

    private Button transparentCheck;

    public TransparentColorEditorPart(StyleProperty<TransparentColor> property,
            TupleStyle style)
    {
        super(property, style);
    }

    public void createWidgets(Composite parent, int columns) {
        createDefinedCheck(parent);

        final Composite panel = createValuesPanel(parent, columns - 1);

        colorSelector = new ColorSelector(panel);

        transparentCheck = new Button(panel, SWT.CHECK);
        transparentCheck.setText("Transparent");
        transparentCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                transparentCheckChanged();
            }
        });

        final TransparentColor transparentColor = property.get(style);
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
                final Device device = style.getUI().getDevice();
                color = new Color(device, rgb);
            }
            return new TransparentColor(color);
        }
    }
}
