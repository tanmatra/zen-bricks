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

    private Preferences getStyleNode(Preferences preferences) {
        return preferences.node(key);
    }

    public final void load(Preferences preferences) {
        loadImpl(getStyleNode(preferences));
    }

    public final void save(Preferences preferences) {
        saveImpl(getStyleNode(preferences));
    }

    protected abstract void loadImpl(Preferences node);

    protected abstract void saveImpl(Preferences node);

    public abstract IStyleEditor createEditor();

    public abstract void dispose();
}
