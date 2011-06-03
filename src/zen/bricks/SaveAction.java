package zen.bricks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

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
        final Brick document = mainWindow.editor.document;
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
            writer.write(tupleBrick.getText());
            writer.write('\n');
            final int count = tupleBrick.childrenCount() - 1;
            for (int i = 0; i < count; i++) {
                writeBrick(tupleBrick.getChild(i), writer);
            }
            writer.write(")\n");
        } else if (brick instanceof LineBreak) {
            writer.write("|\n");
        } else {
            writer.write('[');
            writer.write(brick.getClass().getName());
            writer.write("]\n");
        }
    }
}
