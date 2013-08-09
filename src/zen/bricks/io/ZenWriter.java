package zen.bricks.io;

import java.io.IOException;
import zen.bricks.Brick;

public interface ZenWriter
{
    public void write(Brick brick) throws IOException;
}
