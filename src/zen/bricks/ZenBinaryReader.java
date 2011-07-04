package zen.bricks;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.util.ArrayList;

import zen.bricks.utils.VarInt;

public class ZenBinaryReader
{
    private final InputStream input;

    private final ArrayList<String> pool = new ArrayList<String>();

    public ZenBinaryReader(InputStream input) {
        this.input = input;
    }

    public void close() throws IOException {
        input.close();
    }

    public Brick read() throws IOException {
        final int version = input.read();
        if (version != ZenBinaryProtocol.VERSION) {
            throw new IOException("Wrong binary file version: " + version);
        }
        return readBrick(null);
    }

    private Brick readBrick(ContainerBrick parent) throws IOException {
        for (;;) {
            final int marker = input.read();
            switch (marker) {
                case ZenBinaryProtocol.MARKER_STRING:
                    final String str = readUTF();
                    pool.add(str);
                    break;
                case ZenBinaryProtocol.MARKER_TUPLE:
                    final int strIndex = VarInt.decodeInt(input);
                    final String text = pool.get(strIndex);
                    final TupleBrick tuple = new TupleBrick(parent, text);
                    final int count = VarInt.decodeInt(input);
                    for (int i = 0; i < count; i++) {
                        final Brick child = readBrick(tuple);
                        tuple.appendChild(child);
                    }
                    return tuple;
                case ZenBinaryProtocol.MARKER_LINEBREAK:
                    return new LineBreak(parent);
                case ZenBinaryProtocol.MARKER_UNKNOWN:
                    final int classIdx = VarInt.decodeInt(input);
                    final String className = pool.get(classIdx);
                    return new TupleBrick(parent, "[" + className + "]");
                case ZenBinaryProtocol.MARKER_STRINGLIST:
                    final int strCount = VarInt.decodeInt(input);
                    for (int i = 0; i < strCount; i++) {
                        pool.add(readUTF());
                    }
                    break;
                default:
                    throw new IOException("Bad marker: " + marker);
            }
        }
    }

    private String readUTF() throws IOException {
        final int utfLen = VarInt.decodeInt(input);
        final byte[] bytes = readFully(utfLen);
        final char[] chars = new char[utfLen];
        int charPtr = 0;

        for (int bytePtr = 0; bytePtr < utfLen;) {
            final int c = bytes[bytePtr] & 0xFF;
            if (c <= 127) {    // 0xxxxxxx
                chars[charPtr++] = (char) c;
                bytePtr++;
            } else {
                final int c2;
                final int c3;
                switch (c >> 4) {
                    case 12: case 13: // 110x_xxxx, 10xx_xxxx
                        bytePtr += 2;
                        if (bytePtr > utfLen) {
                            throw partialCharException();
                        }
                        c2 = bytes[bytePtr - 1];
                        if ((c2 & 0xC0) != 0x80) {
                            throw malformedByteException(bytePtr);
                        }
                        chars[charPtr++] = (char) ((c & 0x1F) << 6 | c2 & 0x3F);
                        break;
                    case 14: // 1110_xxxx, 10xx_xxxx, 10xx_xxxx
                        bytePtr += 3;
                        if (bytePtr > utfLen) {
                            throw partialCharException();
                        }
                        c2 = bytes[bytePtr - 2];
                        c3 = bytes[bytePtr - 1];
                        if (((c2 & 0xC0) != 0x80) || ((c3 & 0xC0) != 0x80)) {
                            throw malformedByteException(bytePtr - 1);
                        }
                        chars[charPtr++] = (char) ((c  & 0x0F) << 12 |
                                                   (c2 & 0x3F) <<  6 |
                                                   (c3 & 0x3F));
                        break;
                    default: // 10xx_xxxx,  1111_xxxx
                        throw malformedByteException(bytePtr);
                }
            }
        }

        return new String(chars, 0, charPtr);
    }

    private static IOException partialCharException() {
        return new UTFDataFormatException(
                "malformed input: partial character at end");
    }

    private static IOException malformedByteException(int ptr) {
        return new UTFDataFormatException(
                "malformed input around byte " + ptr);
    }

    private byte[] readFully(int count) throws IOException {
        final byte[] array = new byte[count];
        int pos = 0;
        while (pos < count) {
            final int readed = input.read(array, pos, count - pos);
            if (readed < 0) {
                throw new EOFException();
            }
            pos += readed;
        }
        return array;
    }
}
