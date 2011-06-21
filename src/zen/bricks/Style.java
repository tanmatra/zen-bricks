package zen.bricks;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import zen.bricks.styleeditor.IStyleEditor;

public abstract class Style
{
    // ================================================================== Fields

    private final String key;

    private final String name;

    protected final UI ui;

    private final Style parent;

    private final List<Style> children = new ArrayList<Style>(0);

    // ============================================================ Constructors

    public Style(UI ui, String key, String name) {
        parent = null;
        this.ui = ui;
        this.key = key;
        this.name = name;
    }

    public Style(Style parent, String key, String name) {
        this.parent = parent;
        this.ui = parent.ui;
        this.key = key;
        this.name = name;
        parent.children.add(this);
    }

    // ================================================================= Methods

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

    public Style getParent() {
        return parent;
    }

    public List<Style> getChildren() {
        return children;
    }

    public boolean isTopLevel() {
        return children.size() == 0;
    }
}
