package zen.bricks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

public class OpenAction extends Action
{
    private static final String[] FILTER_NAMES = new String[] { "Zen files" };

    private static final String[] FILTER_EXTENSIONS = new String[] { "*.zen" };

    private final MainWindow mainWindow;

    public OpenAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    public void run() {
        final FileDialog dialog =
                new FileDialog(mainWindow.getShell(), SWT.OPEN | SWT.SINGLE);
        dialog.setFilterPath("samples/");
        dialog.setFilterExtensions(FILTER_EXTENSIONS);
        dialog.setFilterNames(FILTER_NAMES);

        final String fileName = dialog.open();
        if (fileName == null) {
            return;
        }

        final Brick document;
        try {
            document = load(fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error loading file");
            return;
        }
        mainWindow.editor.setDocument(document);
        mainWindow.setEditorFileName(fileName);
    }

    private Brick load(String fileName) throws IOException {
        final InputStream input = new FileInputStream(fileName);
        try {
            final Reader inputReader = new InputStreamReader(input, "UTF-8");
            try {
                final PushbackReader reader = new PushbackReader(inputReader);
                try {
                    return readBrick(reader, null);
                } finally {
                    reader.close();
                }
            } finally {
                inputReader.close();
            }
        } finally {
            input.close();
        }
    }

    private Brick readBrick(PushbackReader reader, TupleBrick parent)
            throws IOException
    {
        final int c = reader.read();
        if (c < 0) {
            return null;
        } else if (c == '(') {
            final String text = readTupleText(reader);
            final TupleBrick tupleBrick = new TupleBrick(parent, text);
            for (;;) {
                final Brick brick = readBrick(reader, tupleBrick);
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
        } else {
            throw new IOException("Invalid char: " + (char) c);
        }
    }

    private String readTupleText(PushbackReader reader) throws IOException {
        final StringBuilder buffer = new StringBuilder();
        LOOP: for (;;) {
            final int c = reader.read();
            if (c < 0) {
                break;
            }
            switch (c) {
                case '(': case ')': case '[': case ']': case '\n':
                    reader.unread(c);
                    break LOOP;
                case '\\':
                    final int c2 = reader.read();
                    if (c2 < 0) {
                        throw new IOException("Unexpected EOF in escape char");
                    }
                    switch (c2) {
                        case '\\': case '(': case ')': case '[': case ']':
                            buffer.append((char) c2);
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
