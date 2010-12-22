package zen.bricks;

import zen.bricks.styleeditor.IStyleEditor;

public abstract class Style
{
    private final String name;

    protected final UI ui;

    public Style(UI ui, String name) {
        this.ui = ui;
        this.name = name;
    }

    public UI getUI() {
        return ui;
    }

    public String getName() {
        return name;
    }

    public abstract IStyleEditor createEditor();

    public abstract void dispose();
}
