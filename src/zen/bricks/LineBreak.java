package zen.bricks;

import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import zen.bricks.properties.ColorProperty;
import zen.bricks.styleeditor.IStyleEditor;
import zen.bricks.styleeditor.PropertiesListEditor;

public class LineBreak extends Brick
{
    // ========================================================== Nested Classes

    static class LineBreakStyle extends Style
    {
        Color color;

        public LineBreakStyle(UI ui, String key, String name) {
            super(ui, key, name);
        }

        protected void loadImpl(Preferences node) {
            COLOR.load(this, node);
        }

        protected void saveImpl(Preferences node) {
            COLOR.save(this, node);
        }

        void setRGB(RGB rgb) {
            if (color != null) {
                color.dispose();
            }
            if (rgb != null) {
                color = new Color(ui.getDevice(), rgb);
            } else {
                color = null;
            }
        }

        RGB getRGB() {
            return (color == null) ? null : color.getRGB();
        }

        public IStyleEditor createEditor() {
            return new PropertiesListEditor<LineBreakStyle>(
                    this, ALL_PROPERTIES);
        }

        public void dispose() {
            if (color != null) {
                color.dispose();
                color = null;
            }
        }
    }

    // ============================================================ Class Fields

    static final ColorProperty<LineBreakStyle> COLOR =
            new ColorProperty<LineBreakStyle>("color", "Color")
    {
        public RGB get(LineBreakStyle style) {
            return style.getRGB(); }
        public void set(LineBreakStyle style, RGB rgb) {
            style.setRGB(rgb); }
    };

    static final List<? extends Property<LineBreakStyle, ?>> ALL_PROPERTIES =
            Arrays.asList(COLOR);

    // ============================================================ Constructors

    public LineBreak(ContainerBrick parent) {
        super(parent);
        resize(2, 4);
    }

    // ================================================================= Methods

    protected void paint(GC gc, int baseX, int baseY, Rectangle clipping,
            Editor editor)
    {
        final Color color = editor.getUI().getLineBreakStyle().color;
        if (color != null) {
            gc.setBackground(color);
            gc.fillRectangle(baseX, baseY, getWidth(), getHeight());
        }
    }

    public boolean doLayout(Editor editor, boolean force) {
        return force;
    }
}
