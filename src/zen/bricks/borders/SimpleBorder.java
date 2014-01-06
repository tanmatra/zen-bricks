package zen.bricks.borders;

import java.util.prefs.Preferences;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import zen.bricks.Border;
import zen.bricks.BorderFactory;
import zen.bricks.Brick;
import zen.bricks.ColorUtil;
import zen.bricks.Editor;
import zen.bricks.Property;
import zen.bricks.StyleChain;
import zen.bricks.TupleBrick;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.IStyleEditor;

public class SimpleBorder extends Border
{
    // ============================================================ Class Fields

    static final String COLOR_KEY = "color";

    // ========================================================== Nested Classes

    public static class Factory <B extends SimpleBorder>
            extends BorderFactory<B>
    {
        @Override
        public String getName() {
            return "simple";
        }

        @Override
        public String getTitle() {
            return "Simple";
        }

        @Override
        protected B newBorder(UI ui) {
            return (B) new SimpleBorder(this, ui);
        }

        @Override
        public IStyleEditor createStyleEditor(
                TupleStyle style, Property<TupleStyle, B> property)
        {
            return new SimpleBorder.StyleEditor<B>(this, style, property);
        }
    }

    // -------------------------------------------------------------------------

    public static class StyleEditor<S extends SimpleBorder>
            extends BorderFactory.StyleEditor<S>
    {
        private Label label;
        private ColorSelector colorSelector;

        StyleEditor(Factory<S> factory, TupleStyle style,
                Property<TupleStyle, S> property)
        {
            super(factory, style, property);
        }

        @Override
        protected void createContent(Composite parent) {
            label = new Label(parent, SWT.NONE);
            label.setText("Color:");

            colorSelector = new ColorSelector(parent);
            if (sourceBorder instanceof SimpleBorder) {
                final SimpleBorder simpleBorder = (SimpleBorder) sourceBorder;
                colorSelector.setColorValue(simpleBorder.getColor());
            }
        }

        @Override
        protected void configure(S border) {
            final RGB value = colorSelector.getColorValue();
            if (value != null) { // better handling?
                border.setColor(value);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    // ================================================================== Fields

    protected Color color;

    // ============================================================ Constructors

    protected <T extends SimpleBorder> SimpleBorder(
            BorderFactory<T> factory, UI ui)
    {
        super(factory, ui);
    }

    // ================================================================= Methods

    @Override
    public void dispose() {
        if (color != null) {
            color.dispose();
            color = null;
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Override
    public void init(Preferences preferences, UI ui) {
        final String colorStr = preferences.get(COLOR_KEY, null);
        setColor(ColorUtil.parse(ui.getDevice(), colorStr));
    }

    @Override
    public void paintBackground(GC gc, int x, int y, Brick brick,
            Rectangle clipping, Editor editor)
    {
        final TupleBrick tupleBrick = (TupleBrick) brick;
        final StyleChain styleChain = editor.getStyleChain(tupleBrick);
        final Color backgroundColor =
                styleChain.find(TupleStyle.BACKGROUND).getBackgroundColor();
        ui.prepareGraphicsPaint(gc);
        paintBackground(gc, x, y, brick, backgroundColor);
    }

    protected void paintBackground(GC gc, int x, int y, Brick brick,
            Color backgroundColor)
    {
        gc.setBackground(backgroundColor);
        gc.fillRectangle(x, y, brick.getWidth(), brick.getHeight());
    }

    @Override
    public void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping, Editor editor)
    {
        ui.prepareGraphicsPaint(gc);
        gc.setForeground(color);
        gc.drawRectangle(x, y, brick.getWidth() - 1, brick.getHeight() - 1);
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public RGB getColor() {
        return color.getRGB();
    }

    public void setColor(RGB rgb) {
        this.color = new Color(ui.getDevice(), rgb);
    }

    @Override
    public void save(Preferences prefs) {
        prefs.put(COLOR_KEY, ColorUtil.format(color));
    }
}
