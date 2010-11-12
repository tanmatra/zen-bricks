package zen.bricks.styleeditor;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

class ColorEditorPart extends CheckedEditorPart
{
    private final Color color;
    ColorSelector colorSelector;
    private final boolean allowTransparent;
    private Button transparentCheck;

    ColorEditorPart(Color color, String title) {
        super(title);
        this.color = color;
        allowTransparent = false;
    }

    ColorEditorPart(Color color, String title, boolean allowTransparent) {
        super(title);
        this.color = color;
        this.allowTransparent = allowTransparent;
    }

    int getNumColumns() {
        return allowTransparent ? 3 : 2;
    }

    void createWidgets(Composite parent, int numColumns) {
        createEnabledCheck(parent,
            allowTransparent ? numColumns - 2 : numColumns - 1);

        if (allowTransparent) {
            transparentCheck = new Button(parent, SWT.CHECK);
            transparentCheck.setText("Transparent");
            transparentCheck.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    testColorSelectorEnabled();
                }
            });
            gridData().applyTo(transparentCheck);
        }

        colorSelector = new ColorSelector(parent);
        gridData().applyTo(colorSelector.getButton());

        setEnabled(color != null);
        if (color != null) {
            colorSelector.setColorValue(color.getRGB());
        }
        testTransparentCheckEnabled();
        testColorSelectorEnabled();
    }

    protected void enabledCheckSelected(boolean selected) {
        testTransparentCheckEnabled();
        testColorSelectorEnabled();
    }

    void testTransparentCheckEnabled() {
        if (transparentCheck != null) {
            transparentCheck.setEnabled(isEnabled());
        }
    }

    void testColorSelectorEnabled() {
        final boolean enabled;
        if (isEnabled()) {
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

    protected RGB getRGB() {
        return isEnabled() ? colorSelector.getColorValue() : null;
    }

    protected boolean isTransparent() {
        return (transparentCheck != null) ?
            transparentCheck.getSelection() :
            false;
    }

    void apply() {
    }

    void cancel() {
    }
}
