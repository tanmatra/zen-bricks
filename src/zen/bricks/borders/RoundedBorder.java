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
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.IStyleEditor;

public class RoundedBorder extends SimpleBorder
{
    // ========================================================== Nested Classes

    public static class Factory <B extends RoundedBorder>
            extends SimpleBorder.Factory<B>
    {
        public String getName() {
            return "rounded";
        }

        public String getTitle() {
            return "Rounded";
        }

        protected B newBorder(UI ui) {
            return (B) new RoundedBorder(this, ui);
        }

        protected void init(B border, Preferences preferences, UI ui) {
            super.init(border, preferences, ui);
            border.arcSize = preferences.getInt("arcSize", 0);
        }

        public IStyleEditor createStyleEditor(TupleStyle style,
                StyleProperty<B> property)
        {
            return new RoundedBorder.StyleEditor<B>(this, style, property);
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    static class StyleEditor<S extends RoundedBorder>
            extends SimpleBorder.StyleEditor<S>
    {
        StyleEditor(Factory<S> factory,
                    TupleStyle style, StyleProperty<S> property)
        {
            super(factory, style, property);
        }

        private Spinner spinner;

        protected void createContent(Composite parent) {
            super.createContent(parent);

            new Label(parent, SWT.NONE).setText("Arc radius:");

            spinner = new Spinner(parent, SWT.BORDER);
            if (sourceBorder instanceof RoundedBorder) {
                final RoundedBorder roundedBorder = (RoundedBorder) sourceBorder;
                spinner.setSelection(roundedBorder.arcSize);
            }
        }

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

    protected void paintBackground(GC gc, int x, int y, Brick brick,
            Color background)
    {
        gc.setBackground(background);
        gc.fillRoundRectangle(x, y,
                brick.getWidth(), brick.getHeight(),
                arcSize, arcSize);
    }

    protected void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping, Color foreground)
    {
        gc.setForeground(color);
        gc.drawRoundRectangle(x, y,
                brick.getWidth() - 1, brick.getHeight() - 1,
                arcSize, arcSize);
    }
}
