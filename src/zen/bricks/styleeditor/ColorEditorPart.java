package zen.bricks.styleeditor;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public class ColorEditorPart extends CheckedEditorPart<RGB>
{
    private RGB color;
    ColorSelector colorSelector;
    private final boolean allowTransparent;
    private Button transparentCheck;
    private final Boolean background;

    public ColorEditorPart(StyleProperty<RGB> property, TupleStyle style) {
        super(property, style);
        this.allowTransparent = false;
        color = property.get(style);
        background = (color == null) ? null : true;
    }

    public ColorEditorPart(StyleProperty<RGB> property, TupleStyle style,
                           Boolean textBackground) {
        super(property, style);
        this.background = textBackground;
        this.allowTransparent = true;
        color = property.get(style);
    }

    void createWidgets(Composite parent, int columns) {
        createDefinedCheck(parent);

        final Composite panel = createValuesPanel(parent, columns - 1);

        colorSelector = new ColorSelector(panel);

        if (allowTransparent) {
            transparentCheck = new Button(panel, SWT.CHECK);
            transparentCheck.setText("Transparent");
            transparentCheck.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    testColorSelectorEnabled();
                }
            });
            transparentCheck.setSelection(Boolean.FALSE.equals(background));
        }

        final boolean defined = (background != null);
        setDefined(defined);
        if (color != null) {
            colorSelector.setColorValue(color);
        }
        testTransparentCheckEnabled(defined);
        testColorSelectorEnabled();
    }

    @Override
    protected void definedCheckChanged(boolean defined) {
        testTransparentCheckEnabled(defined);
        testColorSelectorEnabled();
    }

    void testTransparentCheckEnabled(boolean defined) {
        if (transparentCheck != null) {
            transparentCheck.setEnabled(defined);
        }
    }

    void testColorSelectorEnabled() {
        final boolean enabled;
        if (isDefined()) {
            if (transparentCheck == null) {
                enabled = true;
            } else {
                enabled = !transparentCheck.getSelection();
            }
        } else {
            enabled = false;
        }
        colorSelector.setEnabled(enabled);
    }

    public RGB getValue() {
        return isDefined() ? colorSelector.getColorValue() : null;
    }

    public Boolean getBackground() {
        return !isDefined() ? null :
                transparentCheck.getSelection() ? false : true;
    }
}
