package zen.bricks;

import java.util.prefs.Preferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import zen.bricks.styleeditor.IStyleEditor;

public abstract class BorderFactory <V extends Border>
{
    public abstract String getName();

    public abstract String getTitle();

    public V createBorder(UI ui, Preferences preferences) {
        final V border = newBorder(ui);
        border.init(preferences, ui);
        return border;
    }

    protected abstract V newBorder(UI ui);

    public abstract IStyleEditor createStyleEditor(
            TupleStyle style, Property<TupleStyle, V> property);

    // ========================================================== Nested Classes

    public static abstract class StyleEditor<T extends Border>
            implements IStyleEditor
    {
        private final TupleStyle style;

        private final Property<TupleStyle, T> property;

        /* This value is source only, do not modify or set it. */
        protected final Border sourceBorder;

        private final BorderFactory<T> factory;

        private Group group;

        protected StyleEditor(BorderFactory<T> factory, TupleStyle style,
                Property<TupleStyle, T> property)
        {
            this.factory = factory;
            this.style = style;
            this.property = property;
            this.sourceBorder = property.get(style);
        }

        @Override
        public final Control createControl(Composite parent) {
            group = new Group(parent, SWT.NONE);
            group.setLayout(new GridLayout(2, false));
            group.setText("Border properties");

            createContent(group);
            return group;
        }

        protected abstract void createContent(Composite parent);

        @Override
        public void cancel() {
            // do nothing
        }

        @Override
        public void apply() {
            T border = factory.newBorder(style.getUI());
            try {
                configure(border);
            } catch (IllegalArgumentException e) {
                border.dispose();
                border = null;
            }
            if (border != null) {
                property.set(style, border);
            }
        }

        /**
         * @throws IllegalArgumentException
         */
        protected abstract void configure(T border);
    }
}
