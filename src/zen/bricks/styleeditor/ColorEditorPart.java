package zen.bricks.styleeditor;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

class ColorEditorPart extends StyleEditorPart
{
    private final Color color;
    private final String title;
    Button check;
    ColorSelector colorSelector;
    private final boolean allowTransparent;
    private Button transparentCheck;

    ColorEditorPart(Color color, String title) {
        this.color = color;
        this.title = title;
        allowTransparent = false;
    }

    ColorEditorPart(Color color, String title, boolean allowTransparent) {
        this.color = color;
        this.title = title;
        this.allowTransparent = allowTransparent;
    }

    int getNumColumns() {
        return allowTransparent ? 3 : 2;
    }

    void createWidgets(Composite parent, int numColumns) {
        check = new Button(parent, SWT.CHECK);
        check.setText(title);
        check.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                testTransparentCheckEnabled();
                testColorSelectorEnabled();
            }
        });
        gridData(numColumns - (allowTransparent ? 2 : 1)).applyTo(check);

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

        check.setSelection(color != null);
        if (color != null) {
            colorSelector.setColorValue(color.getRGB());
        }
        testTransparentCheckEnabled();
        testColorSelectorEnabled();
    }

    void testTransparentCheckEnabled() {
        if (transparentCheck != null) {
            transparentCheck.setEnabled(check.getSelection());
        }
    }

    void testColorSelectorEnabled() {
        final boolean enabled;
        if (check.getSelection()) {
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

    protected boolean isDefined() {
        return check.getSelection();
    }

    protected RGB getRGB() {
        return isDefined() ? colorSelector.getColorValue() : null;
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
