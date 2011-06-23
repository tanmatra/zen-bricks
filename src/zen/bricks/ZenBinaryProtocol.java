package zen.bricks;

public class ZenBinaryProtocol
{
    public static final int VERSION = 1;

    public static final int MARKER_STRING        = 1;
    public static final int MARKER_TUPLE        = 2;
    public static final int MARKER_LINEBREAK    = 3;
    public static final int MARKER_UNKNOWN        = 4;

    private ZenBinaryProtocol() {
    }
}
