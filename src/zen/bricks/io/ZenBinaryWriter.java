package zen.bricks.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zen.bricks.Brick;
import zen.bricks.LineBreak;
import zen.bricks.TupleBrick;
import zen.bricks.utils.VarInt;

public class ZenBinaryWriter implements ZenWriter
{
    private final OutputStream output;

    private int lastPoolIndex = -1;

    private final Map<String, Integer> pool = new HashMap<String, Integer>();

    public ZenBinaryWriter(OutputStream output) {
        this.output = output;
    }

    @Override
    public void close() throws IOException {
        output.flush();
    }

    @Override
    public void write(Brick brick) throws IOException {
        output.write(ZenBinaryProtocol.VERSION);
        collectStrings(brick);
        writeBrick(brick);
    }

    private void collectStrings(Brick brick) throws IOException {
        final ArrayList<String> list = new ArrayList<String>();
        scanStrings(brick, list);
        final int count = list.size();
        if (count != 0) {
            output.write(ZenBinaryProtocol.MARKER_STRINGLIST);
            VarInt.encodeInt(output, count);
            for (int i = 0; i < count; i++) {
                writeUTF(list.get(i));
            }
        }
    }

    private void scanStrings(Brick brick, List<String> list) {
        if (brick instanceof TupleBrick) {
            final TupleBrick tuple = (TupleBrick) brick;
            final String text = tuple.getText();
            Integer index = pool.get(text);
            if (index == null) {
                index = ++lastPoolIndex;
                pool.put(text, index);
                list.add(text);
            }
            final int count = tuple.getChildCount();
            for (int i = 0; i < count; i++) {
                scanStrings(tuple.getChild(i), list);
            }
        }
    }

    private void writeBrick(Brick brick) throws IOException {
        if (brick instanceof TupleBrick) {
            final TupleBrick tuple = (TupleBrick) brick;
            final int strIndex = recordString(tuple.getText());
            final int count = tuple.getChildCount();
            if (count == 0) {
                output.write(ZenBinaryProtocol.MARKER_ATOM);
                VarInt.encodeInt(output, strIndex);
            } else {
                output.write(ZenBinaryProtocol.MARKER_TUPLE);
                VarInt.encodeInt(output, strIndex);
                VarInt.encodeInt(output, count);
                for (int i = 0; i < count; i++) {
                    writeBrick(tuple.getChild(i));
                }
            }
        } else if (brick instanceof LineBreak) {
            output.write(ZenBinaryProtocol.MARKER_LINEBREAK);
        } else {
            final int strIndex = recordString(brick.getClass().getName());
            output.write(ZenBinaryProtocol.MARKER_UNKNOWN);
            VarInt.encodeInt(output, strIndex);
        }
    }

    private int recordString(String text) throws IOException {
        Integer index = pool.get(text);
        if (index == null) {
            index = ++lastPoolIndex;
            pool.put(text, index);
            output.write(ZenBinaryProtocol.MARKER_STRING);
            writeUTF(text);
        }
        return index;
    }

    private static int utfLength(String str) {
        final int length = str.length();
        int result = 0;
        for (int i = 0; i < length; i++) {
            final int c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                result++;
            } else if (c > 0x07FF) {
                result += 3;
            } else {
                result += 2;
            }
        }
        return result;
    }

    private void writeUTF(String str) throws IOException {
        VarInt.encodeInt(output, utfLength(str));
        final int length = str.length();
        for (int i = 0; i < length; i++) {
            final int c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                output.write(c);
            } else if (c > 0x07FF) {
                output.write(0xE0 | ((c >> 12) & 0x0F));
                output.write(0x80 | ((c >>  6) & 0x3F));
                output.write(0x80 | ((c >>  0) & 0x3F));
            } else {
                output.write(0xC0 | ((c >>  6) & 0x1F));
                output.write(0x80 | ((c >>  0) & 0x3F));
            }
        }
    }
}
