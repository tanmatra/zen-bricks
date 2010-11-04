package zen.bricks;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface ITextStyleEditor
{
    public void createControl(Composite parent);
    
    public Control getControl();
    
    public void apply();
    
    public void cancel();
}
