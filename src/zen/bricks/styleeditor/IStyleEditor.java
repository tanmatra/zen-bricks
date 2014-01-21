package zen.bricks.styleeditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IStyleEditor
{
    public Control createControl(Composite parent);

    public void apply();

    public void cancel();
}
