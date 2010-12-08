package zen.bricks.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PropertiesPreferences extends AbstractPreferences
{
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        FileInputStream fis =
                new FileInputStream("themes/default.theme.properties");
        try {
            props.load(fis);
        } finally {
            fis.close();
        }
        PropertiesPreferences prefs = new PropertiesPreferences(props);
        System.out.println("antialias = " + prefs.get("antialias", null));

        Preferences borderNode = prefs.node("border");
        printKeys(borderNode);
        borderNode.removeNode();
        printKeys(borderNode);

        Preferences stylesNode = prefs.node("styles");
        System.out.println("class = " + stylesNode.get("class", null));

        String[] childrenNames = stylesNode.childrenNames();
        for (final String childName : childrenNames) {
            System.out.format("Style '%s'%n", childName);
            Preferences subnode = stylesNode.node(childName);
            System.out.println("Style color = " + subnode.get("color", null));
        }
        System.out.println();
    }

    private static void printKeys(Preferences node)
            throws BackingStoreException
    {
        for (final String attr : node.keys()) {
            System.out.format("Border attr: '%s'%n", attr);
        }
    }

    public static Preferences load(String fileName) throws IOException {
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
}