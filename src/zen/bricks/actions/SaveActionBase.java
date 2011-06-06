package zen.bricks.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.jface.action.Action;

import zen.bricks.Brick;
import zen.bricks.LineBreak;
import zen.bricks.MainWindow;
import zen.bricks.TupleBrick;

public class SaveActionBase extends Action
{
    protected static final String[] FILTER_NAMES =
            new String[] { "Zen files" };

    protected static final String[] FILTER_EXTENSIONS =
            new String[] { "*.zen" };

    protected static final String DEFAULT_PATH = "samples/";

    protected final MainWindow mainWindow;

    protected SaveActionBase(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    protected void save(Brick document, String fileName) throws IOException {
        final OutputStream output = new FileOutputStream(fileName);
        try {
            final Writer writer = new OutputStreamWriter(output, "UTF-8");
            try {
                writeBrick(document, writer);
            } finally {
                writer.close();
            }
        } finally {
            output.close();
        }
    }

    private void writeBrick(Brick brick, Writer writer) throws IOException {
        if (brick instanceof TupleBrick) {
            final TupleBrick tupleBrick = (TupleBrick) brick;
            writer.write('(');
            writeTupleText(writer, tupleBrick.getText());
            final int count = tupleBrick.childrenCount() - 1;
            for (int i = 0; i < count; i++) {
                writeBrick(tupleBrick.getChild(i), writer);
            }
            writer.write(')');
        } else if (brick instanceof LineBreak) {
            writer.write('\n');
        } else {
            writer.write('[');
            writer.write(brick.getClass().getName());
            writer.write(']');
        }
    }

    private void writeTupleText(Writer writer, String text) throws IOException {
        final int length = text.length();
        for (int i = 0; i < length; i++) {
            final char c = text.charAt(i);
            switch (c) {
                case '\n':
                    writer.write("\\n");
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
}
