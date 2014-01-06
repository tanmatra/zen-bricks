package zen.bricks.borders;

import java.util.prefs.Preferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import zen.bricks.BorderFactory;
import zen.bricks.Brick;
import zen.bricks.Editor;
import zen.bricks.Property;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.IStyleEditor;

public class RoundedBorder extends SimpleBorder
{
    // ============================================================ Class Fields

    static final String ARC_SIZE_KEY = "arcSize";

    // ========================================================== Nested Classes

    public static class Factory <B extends RoundedBorder>
            extends SimpleBorder.Factory<B>
    {
        @Override
        public String getName() {
            return "rounded";
        }

        @Override
        public String getTitle() {
            return "Rounded";
        }

        @Override
        protected B newBorder(UI ui) {
            return (B) new RoundedBorder(this, ui);
        }

        @Override
        public IStyleEditor createStyleEditor(
                TupleStyle style, Property<TupleStyle, B> property)
        {
            return new RoundedBorder.StyleEditor<B>(this, style, property);
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    static class StyleEditor<S extends RoundedBorder>
            extends SimpleBorder.StyleEditor<S>
    {
        StyleEditor(Factory<S> factory,
                    TupleStyle style, Property<TupleStyle, S> property)
        {
            super(factory, style, property);
        }

        private Spinner spinner;

        @Override
        protected void createContent(Composite parent) {
            super.createContent(parent);

            new Label(parent, SWT.NONE).setText("Arc radius:");

            spinner = new Spinner(parent, SWT.BORDER);
            if (sourceBorder instanceof RoundedBorder) {
                final RoundedBorder roundedBorder = (RoundedBorder) sourceBorder;
                spinner.setSelection(roundedBorder.arcSize);
            }
        }

        @Override
        protected void configure(S border) {
            super.configure(border);
            border.arcSize = spinner.getSelection();
        }
    }

    // ================================================================== Fields

    int arcSize;

    // ============================================================ Constructors

    protected <T extends RoundedBorder> RoundedBorder(
            BorderFactory<T> factory, UI ui)
    {
        super(factory, ui);
    }

    // ================================================================= Methods

    @Override
    public void init(Preferences preferences, UI ui) {
        super.init(preferences, ui);
        arcSize = preferences.getInt(ARC_SIZE_KEY, 0);
    }

    @Override
    protected void paintBackground(GC gc, int x, int y, Brick brick,
            Color background)
    {
        ui.prepareGraphicsPaint(gc);
        gc.setBackground(background);
        gc.fillRoundRectangle(x, y,
                brick.getWidth(), brick.getHeight(),
                arcSize, arcSize);
    }

    @Override
    public void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping, Editor editor)
    {
        ui.prepareGraphicsPaint(gc);
        gc.setForeground(color);
        gc.drawRoundRectangle(x, y,
                brick.getWidth() - 1, brick.getHeight() - 1,
                arcSize, arcSize);
    }

    @Override
    public void save(Preferences prefs) {
        super.save(prefs);
        prefs.putInt(ARC_SIZE_KEY, arcSize);
    }
}
