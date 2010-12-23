package zen.bricks.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

public class PropertiesPreferences extends StoredPreferences
{
    public static StoredPreferences load(String fileName) throws IOException {
        final Properties properties = new Properties();
        final InputStream input = new FileInputStream(fileName);
        try {
            properties.load(input);
        } finally {
            input.close();
        }
        return new PropertiesPreferences(properties);
    }

    // ================================================================== Fields

    private final Properties properties;

    // ============================================================ Constructors

    public PropertiesPreferences(Properties properties) {
        super(null, "");
        this.properties = properties;
    }

    protected PropertiesPreferences(PropertiesPreferences parent, String name) {
        super(parent, name);
        this.properties = parent.properties;
    }

    // ================================================================= Methods

    private String prefixName() {
        if (parent() == null) {
            return "/";
        } else {
            return absolutePath() + "/";
        }
    }

    private String keyName(String key) {
        return prefixName() + key;
    }

    protected void putSpi(String key, String value) {
        properties.put(keyName(key), value);
    }

    protected String getSpi(String key) {
        return properties.getProperty(keyName(key));
    }

    protected void removeSpi(String key) {
        properties.remove(keyName(key));
    }

    protected void removeNodeSpi() throws BackingStoreException {
        final String prefixName = prefixName();
        for (Iterator<String> iter = properties.stringPropertyNames().iterator();
                iter.hasNext();)
        {
            final String key = iter.next();
            if (key.startsWith(prefixName)) {
                iter.remove();
            }
        }
    }

    protected String[] keysSpi() throws BackingStoreException {
        final ArrayList<String> list = new ArrayList<String>();
        final String prefix = prefixName();
        final int prefLen = prefix.length();
        for (final String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                if (key.indexOf('/', prefLen) < 0) {
                    list.add(key.substring(prefLen));
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    protected String[] childrenNamesSpi() throws BackingStoreException {
        final ArrayList<String> list = new ArrayList<String>();
        final String prefix = prefixName();
        final int prefLen = prefix.length();
        for (final String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                final int slashIndex = key.indexOf('/', prefLen);
                if (slashIndex >= 0) {
                    list.add(key.substring(prefLen, slashIndex));
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    protected AbstractPreferences childSpi(String name) {
        return new PropertiesPreferences(this, name);
    }

    protected void syncSpi() throws BackingStoreException {
        // do nothing
    }

    protected void flushSpi() throws BackingStoreException {
        // do nothing
    }

    public void save(String fileName) throws IOException {
        final OutputStream out = new FileOutputStream(fileName);
        try {
            properties.store(out, null);
        } finally {
            out.close();
        }
    }
}
