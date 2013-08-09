package zen.bricks.io;

import java.io.IOException;
import zen.bricks.Brick;
import zen.bricks.ContainerBrick;

public interface ZenReader
{
    public Brick read(ContainerBrick parent) throws IOException;
}
