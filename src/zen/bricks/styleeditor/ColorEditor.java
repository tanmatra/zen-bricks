package zen.bricks.styleeditor;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

class ColorEditor extends StyleEditorPart
{
    private final Color color;
    private final String title;
    Button check;
    ColorSelector colorSelector;

    ColorEditor(Composite parent, Color color, String title) {
        this.color = color;
        this.title = title;

        check = new Button(parent, SWT.CHECK);
        check.setText(title);
        check.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                colorSelector.setEnabled(check.getSelection());
            }
        });
        
        colorSelector = new ColorSelector(parent);
        if (color != null) {
            check.setSelection(true);
            colorSelector.setColorValue(color.getRGB());
        } else {
            colorSelector.setEnabled(false);
        }
    }
    
    protected RGB getRGB() {
        return check.getSelection() ? colorSelector.getColorValue() : null;
    }
    
    void apply() {
        // TODO Auto-generated method stub
    }

    void cancel() {
    }
}
