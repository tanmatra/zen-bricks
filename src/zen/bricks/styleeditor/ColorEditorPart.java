package zen.bricks.styleeditor;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

abstract class ColorEditorPart extends CheckedEditorPart
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

    void createWidgets(Composite parent, int columns) {
        createEnabledCheck(parent);

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
        }

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
}
