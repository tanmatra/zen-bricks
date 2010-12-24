package zen.bricks;

import java.util.prefs.Preferences;

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

    public abstract void load(Preferences preferences);

    public abstract void save(Preferences preferences);

    public abstract IStyleEditor createEditor();

    public abstract void dispose();
}
