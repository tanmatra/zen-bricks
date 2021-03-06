package zen.bricks.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import zen.bricks.Brick;
import zen.bricks.ContainerBrick;
import zen.bricks.LineBreak;
import zen.bricks.TupleBrick;

public class ZenTextReader implements ZenReader
{
    private final PushbackReader reader;

    public ZenTextReader(InputStream input) {
        final Reader streamReader;
        try {
            streamReader = new InputStreamReader(input, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new InternalError();
        }
        reader = new PushbackReader(streamReader);
    }

    @Override
    public Brick read(ContainerBrick parent) throws IOException {
        int c;
        do { // skip spaces
            c = reader.read();
        } while (c == ' ');
        if (c < 0) {
            return null;
        } else if (c == '(') {
            final String text = readTupleText();
            final TupleBrick tupleBrick = new TupleBrick(parent, text);
            for (;;) {
                final Brick brick = read(tupleBrick);
                if (brick != null) {
                    tupleBrick.appendChild(brick);
                }
                final int c2 = reader.read();
                if (c2 < 0) {
                    throw new IOException("Unexpected EOF in tuple");
                } else if (c2 == ')') {
                    break;
                } else {
                    reader.unread(c2);
                }
            }
            return tupleBrick;
        } else if (c == ')') {
            reader.unread(c);
            return null;
        } else if (c == '\n') {
            return new LineBreak(parent);
        } else { // atom
            reader.unread(c);
            final String text = readTupleText();
            final TupleBrick tupleBrick = new TupleBrick(parent, text);
            return tupleBrick;
        }
    }

    private String readTupleText() throws IOException {
        final StringBuilder buffer = new StringBuilder();
        LOOP: for (;;) {
            final int c = reader.read();
            if (c < 0) {
                break;
            }
            switch (c) {
                case '(': case ')': case '[': case ']':
                case '\n': case ' ':
                    reader.unread(c);
                    break LOOP;
                case '\\':
                    final int c2 = reader.read();
                    if (c2 < 0) {
                        throw new IOException("Unexpected EOF in escape char");
                    }
                    switch (c2) {
                        case ' ': case '\\':
                        case '(': case ')': case '[': case ']':
                            buffer.append((char) c2);
                            break;
                        case '_':
                            buffer.append(' ');
                            break;
                        case 'n':
                            buffer.append('\n');
                            break;
                        default:
                            throw new IOException("Illegal escape char");
                    }
                    break;
                default:
                    buffer.append((char) c);
            }
        }
        return buffer.toString();
    }
}
