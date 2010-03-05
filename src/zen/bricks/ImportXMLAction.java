package zen.bricks;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class ImportXMLAction extends Action
{
    private static final class BrickHandler extends DefaultHandler
    {
        private final StringBuilder buffer = new StringBuilder(128);

        TextBrick brick;

        public void startDocument() throws SAXException {
            brick = new TextBrick(null, "<root>");
        }

        public void startElement(String uri, String localName,
                String qName, Attributes attributes) throws SAXException
        {
            handleString();
            TextBrick elementBrick = new TextBrick(brick, qName);

            final int attLen = attributes.getLength();
            for (int i = 0; i < attLen; i++) {
                final String attName = attributes.getQName(i);
                final TextBrick attNameBrick =
                        new TextBrick(elementBrick, attName + " =");
                final String attValue = attributes.getValue(i);
                final TextBrick attValueBrick =
                        new TextBrick(attNameBrick, attValue);
                attValueBrick.lineBreak = false;
            }

            brick = elementBrick;
        }

        private void handleString() {
            if (buffer.length() != 0) {
                final String str = buffer.toString().trim();
                if (str.length() != 0) {
                    @SuppressWarnings("unused")
                    final Brick plainText = new TextBrick(brick, str);
                }
                buffer.setLength(0);
            }
        }

        public void characters(char[] ch, int start, int length)
                throws SAXException
        {
            buffer.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException
        {
            handleString();
            brick = brick.getParent();
        }

        public void endDocument() throws SAXException {
        }
    }

    private final MainWindow mainWindow;

    ImportXMLAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    public void run() {
        final FileDialog dialog =
                new FileDialog(mainWindow.getShell(), SWT.OPEN);
        dialog.setFilterNames(new String[] { "XML files" });
        dialog.setFilterExtensions(new String[] { "*.xml" });
        dialog.setFilterPath(new File("samples/").toString());
        final String fileName = dialog.open();
        if (fileName == null) {
            return;
        }
        try {
            final Editor editor = mainWindow.editor;
            editor.rootBrick.dispose();
            editor.rootBrick = parse(fileName);
            editor.refresh();
            editor.resized();
        } catch (Exception e) {
            final IStatus status =
                    new Status(IStatus.ERROR, "zen.bricks", "Exception", e);
            ErrorDialog.openError(mainWindow.getShell(), "Import error",
                    null, status);
        }
    }

    private TextBrick parse(String fileName) throws Exception {
        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        final SAXParser saxParser = saxFactory.newSAXParser();
        final BrickHandler handler = new BrickHandler();
        saxParser.parse(new File(fileName), handler);
        return handler.brick;
    }
}
