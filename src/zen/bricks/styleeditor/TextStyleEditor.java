package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;

import zen.bricks.ITextStyleEditor;
import zen.bricks.TextStyle;

public class TextStyleEditor implements ITextStyleEditor
{
    // ================================================================== Fields
    
    private final TextStyle textStyle;
    
    private Composite composite;

    private ColorEditor foregroundColorEditor;

    private ColorEditor backgroundColorEditor;

    private FontEditor fontEditor;

    // ============================================================ Constructors
    
    public TextStyleEditor(TextStyle textStyle) {
        this.textStyle = textStyle;
    }
    
    // ================================================================= Methods

    public void createControl(final Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5)
            .applyTo(composite);
        
        foregroundColorEditor = new ColorEditor(composite,
            textStyle.getForegroundColor(), "Foreground color")
        {
            protected void apply() {
                textStyle.setForegroundColor(getRGB());
            }
        };
        
        backgroundColorEditor = new ColorEditor(composite,
            textStyle.getBackgroundColor(), "Background color") 
        {
            protected void apply() {
                textStyle.setBackgroundColor(getRGB());
            }
        };

        fontEditor = new FontEditor(composite, textStyle.getFont(), "Font") 
        {
            protected void apply() {
                textStyle.changeFont(getFontList()[0]); // TODO
            }
        };
    }

    public Control getControl() {
        return composite;
    }

    public void apply() {
        foregroundColorEditor.apply();
        backgroundColorEditor.apply();
        fontEditor.apply();
    }

    public void cancel() {
        foregroundColorEditor.cancel();
        backgroundColorEditor.cancel();
        fontEditor.cancel();
    }
}
