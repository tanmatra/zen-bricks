package zen.bricks;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class ZenTextWriter
{
    private final Writer writer;

    public ZenTextWriter(OutputStream output) {
        try {
            writer = new OutputStreamWriter(output, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new InternalError();
        }
    }

    public void write(Brick brick) throws IOException {
        if (brick instanceof TupleBrick) {
            final TupleBrick tupleBrick = (TupleBrick) brick;
            final int count = tupleBrick.getChildCount() - 1;
            final String text = tupleBrick.getText();
            if (count == 0) {
                if (text.isEmpty()) {
                    writer.write("()");
                } else {
                    writer.write(' ');
                    writeTupleText(text);
                }
            } else {
                writer.write('(');
                writeTupleText(text);
                for (int i = 0; i < count; i++) {
                    write(tupleBrick.getChild(i));
                }
                writer.write(')');
            }
        } else if (brick instanceof LineBreak) {
            writer.write('\n');
        } else {
            writer.write('[');
            writer.write(brick.getClass().getName());
            writer.write(']');
        }
    }

    private void writeTupleText(String text) throws IOException {
        final int length = text.length();
        for (int i = 0; i < length; i++) {
            final char c = text.charAt(i);
            switch (c) {
                case '\n':
                    writer.write("\\n");
                    break;
                case ' ':
                    writer.write("\\_");
                    break;
                case '\\': case '(': case ')': case '[': case ']':
                    writer.write('\\');
                    writer.write(c);
                    break;
                default:
                    writer.write(c);
            }
        }
    }

    public void close() throws IOException {
        writer.close();
    }
}
