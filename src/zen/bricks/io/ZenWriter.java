package zen.bricks.io;

import java.io.Closeable;
import java.io.IOException;
import zen.bricks.Brick;

public interface ZenWriter extends Closeable
{
    public void write(Brick brick) throws IOException;
}
