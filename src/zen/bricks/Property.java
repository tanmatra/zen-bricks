package zen.bricks;

/**
 * @param <T> object type
 * @param <V> value type
 */
public abstract class Property<T, V>
{
    protected final String title;

    protected Property(String title) {
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
}
