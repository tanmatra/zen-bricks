package zen.bricks;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public abstract class Border
{
    private final BorderFactory<?> factory;

    protected final UI ui;

    protected Border(BorderFactory<?> factory, UI ui) {
        this.factory = factory;
        this.ui = ui;
    }

    public abstract void dispose();

    public abstract void paint(GC gc, int x, int y, Brick brick,
                                Rectangle clipping, Editor editor);

    public BorderFactory<?> getFactory() {
        return factory;
    }
}
