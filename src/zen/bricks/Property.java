package zen.bricks;

import java.util.prefs.Preferences;

/**
 * @param <T> object type
 * @param <V> value type
 */
public abstract class Property<T, V>
{
    private final String key;

    private final String title;

    protected Property(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract V get(T object);

    public abstract void set(T object, V value);

    public boolean isDefined(T object) {
        return get(object) != null;
    }

    public abstract void load(T object, Preferences preferences);

    public abstract void save(T object, Preferences preferences);

    protected String read(Preferences preferences) {
        return preferences.get(key, null);
    }

    protected void write(Preferences preferences, String value) {
        if (value == null) {
            preferences.remove(key);
        } else {
            preferences.put(key, value);
        }
    }

    protected void write(Preferences preferences, int value) {
        preferences.putInt(key, value);
    }

    protected Preferences node(Preferences preferences) {
        return preferences.node(key);
    }

//    protected void removeKey(Preferences preferences) {
//        preferences.remove(key);
//    }

    public String toString() {
        return String.format("(Property %s \"%s\")", key, title);
    }
}
