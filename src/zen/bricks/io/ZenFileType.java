package zen.bricks.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum ZenFileType
{
    TEXT("Zen Text files", "*.zen") {
        @Override
        public
        ZenReader openReader(InputStream input) throws IOException {
            return new ZenTextReader(input);
        }

        @Override
        public
        ZenWriter openWriter(OutputStream output) throws IOException {
            return new ZenTextWriter(output);
        }
    },
    BINARY("Zen Binary files", "*.zn") {
        @Override
        public
        ZenReader openReader(InputStream input) throws IOException {
            return new ZenBinaryReader(input);
        }

        @Override
        public
        ZenWriter openWriter(OutputStream output) throws IOException {
            return new ZenBinaryWriter(output);
        }
    };

    private static final String[] FILTER_NAMES;

    private static final String[] FILTER_EXTENSIONS;

    static {
        final ZenFileType[] values = values();
        FILTER_NAMES = new String[values.length];
        FILTER_EXTENSIONS = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            final ZenFileType type = values[i];
            FILTER_NAMES[i] = type.getFilterName();
            FILTER_EXTENSIONS[i] = type.getFilterExtensions();
        }
    }

    public static String[] getAllFilterNames() {
        return FILTER_NAMES;
    }

    public static String[] getAllFilterExtensions() {
        return FILTER_EXTENSIONS;
    }

    private String filterName;

    private String filterExtension;

    private ZenFileType(String filterName, String filterExtension) {
        this.filterName = filterName;
        this.filterExtension = filterExtension;
    }

    String getFilterName() {
        return filterName;
    }

    String getFilterExtensions() {
        return filterExtension;
    }

    public abstract ZenReader openReader(InputStream input) throws IOException;

    public abstract ZenWriter openWriter(OutputStream output) throws IOException;

    public static int indexOf(ZenFileType type) {
        final ZenFileType[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == type) {
                return i;
            }
        }
        return -1;
    }
}
