package zen.bricks;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import zen.bricks.styleeditor.IStyleEditor;

public class LineBreak extends Brick
{
    // ========================================================== Nested Classes

    public static class LineBreakStyle extends Style
    {
        private static final String COLOR_KEY = "color";

        Color color;

        public LineBreakStyle(UI ui, String key, String name) {
            super(ui, key, name);
        }

        protected void loadImpl(Preferences node) {
            color = ColorUtil.parseColor(
                    ui.getDevice(), node.get(COLOR_KEY, null));
        }

        protected void saveImpl(Preferences node) {
            node.put(COLOR_KEY, ColorUtil.format(color));
        }

        public IStyleEditor createEditor() {
            return null; // TODO
        }

        public void dispose() {
            if (color != null) {
                color.dispose();
                color = null;
            }
        }
    }

    // ============================================================ Constructors

    public LineBreak(ContainerBrick parent) {
        super(parent);
        resize(2, 4);
    }

    // ================================================================= Methods

    protected void paint(GC gc, int baseX, int baseY, Rectangle clipping,
            Editor editor)
    {
        final LineBreakStyle style = editor.getUI().getLineBreakStyle();
        gc.setBackground(style.color);
        gc.fillRectangle(baseX, baseY, getWidth(), getHeight());
    }

    public boolean doLayout(Editor editor, boolean force) {
        return force;
    }
}
