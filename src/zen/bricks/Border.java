package zen.bricks;

import java.util.prefs.Preferences;
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

    public abstract void init(Preferences preferences, UI ui);

    public abstract void dispose();

    public abstract void paintBackground(GC gc, int x, int y, Brick brick, Rectangle clipping, Editor editor);

    public abstract void paintBorder(GC gc, int x, int y, Brick brick, Rectangle clipping, Editor editor);

    public BorderFactory<?> getFactory() {
        return factory;
    }

    public abstract void save(Preferences prefs);
}
