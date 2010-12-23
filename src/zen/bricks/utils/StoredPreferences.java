package zen.bricks.utils;

import java.io.IOException;
import java.util.prefs.AbstractPreferences;

public abstract class StoredPreferences extends AbstractPreferences
{
    protected StoredPreferences(AbstractPreferences parent, String name) {
        super(parent, name);
    }

    public abstract void save(String fileName) throws IOException;
}
