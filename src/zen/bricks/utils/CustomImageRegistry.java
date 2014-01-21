package zen.bricks.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class CustomImageRegistry extends ImageRegistry
{
    private final Class<?> klass;
    private final String pathPrefix;

    public CustomImageRegistry(Display display, Class<?> klass) {
        super(display);
        this.klass = klass;
        pathPrefix = null;
    }

    public CustomImageRegistry(Display display, Class<?> klass, String pathPrefix) {
        super(display);
        this.klass = klass;
        this.pathPrefix = pathPrefix;
    }

    public Image load(String path) {
        put(path, path);
        return get(path);
    }

    public void put(String key, String path) {
        if (pathPrefix != null) {
            path = pathPrefix + path;
        }
        final ImageDescriptor descriptor = ImageDescriptor.createFromFile(klass, path);
        put(key, descriptor);
    }
}
