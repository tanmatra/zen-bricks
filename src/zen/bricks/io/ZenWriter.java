package zen.bricks.io;

import java.io.IOException;
import zen.bricks.Brick;

public interface ZenWriter extends AutoCloseable
{
    public void write(Brick brick) throws IOException;

    @Override
    public void close() throws IOException;
}
