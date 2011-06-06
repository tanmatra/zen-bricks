package zen.bricks.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import zen.bricks.Brick;
import zen.bricks.LineBreak;
import zen.bricks.MainWindow;
import zen.bricks.TupleBrick;

public class SaveAction extends Action
{
    private static final String[] FILTER_NAMES = new String[] { "Zen files" };

    private static final String[] FILTER_EXTENSIONS = new String[] { "*.zen" };

    private final MainWindow mainWindow;

    public SaveAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    public void run() {
        final Brick document = mainWindow.getEditor().getDocument();
        if (document == null) {
            return;
        }

        final FileDialog dialog =
                new FileDialog(mainWindow.getShell(), SWT.SAVE);
        dialog.setFilterPath("samples/");
        dialog.setFilterExtensions(FILTER_EXTENSIONS);
        dialog.setFilterNames(FILTER_NAMES);
        dialog.setOverwrite(true);

        final String fileName = dialog.open();
        if (fileName == null) {
            return;
        }

        try {
            save(document, fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error saving file");
        }
        mainWindow.setEditorFileName(fileName);
    }

    private void save(Brick document, String fileName) throws IOException {
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
