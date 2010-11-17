package zen.bricks;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class RootBrick extends Brick
{
    private final Editor editor;

    public RootBrick(Editor editor) {
        super(null);
        this.editor = editor;
    }

    public void paint(GC gc, int baseX, int baseY, UI ui, Rectangle clipping) {
        // TODO Auto-generated method stub
    }
}
