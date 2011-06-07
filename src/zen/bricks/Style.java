package zen.bricks;

import java.util.prefs.Preferences;

import zen.bricks.styleeditor.IStyleEditor;

public abstract class Style
{
    private final String key;

    private final String name;

    protected final UI ui;

    public Style(UI ui, String key, String name) {
        this.ui = ui;
        this.key = key;
        this.name = name;
    }

    public UI getUI() {
        return ui;
    }

    public String getName() {
        return name;
    }

    protected Preferences getStyleNode(Preferences preferences) {
        return preferences.node(key);
    }

    public abstract void load(Preferences preferences);

    public abstract void save(Preferences preferences);

    public abstract IStyleEditor createEditor();

    public abstract void dispose();
}
