package zen.bricks;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class ImportXMLAction extends Action
{
    // ========================================================== Nested Classes

    private static final class BrickHandler extends DefaultHandler
    {
        private static final boolean GROUP_ATTRIBUTES = true;
        private static final String ALL_ATTRS_TEXT = "@";
        private static final String ATTR_SUFFIX = ":";

        private final StringBuilder buffer = new StringBuilder(128);

        TextBrick brick;

        BrickHandler() { }

        public InputSource resolveEntity(String publicId, String systemId)
                throws IOException, SAXException
        {
            return new InputSource(new StringReader(""));
        }

        private static String removeCRs(String str) {
            int i = str.indexOf('\r');
            if (i < 0) {
                return str;
            }
            StringBuilder res = new StringBuilder(str.length());
            res.append(str, 0, i);
            i++;
            while (true) {
                final int p = str.indexOf('\r', i);
                if (p < 0) {
                    res.append(str, i, str.length());
                    break;
                } else {
                    res.append(str, i, p);
                    i = p + 1;
                }
            }
            return res.toString();
        }

        public void startElement(String uri, String localName,
                String qName, Attributes attributes) throws SAXException
        {
            handleString();
            TextBrick elementBrick = new TextBrick(brick, qName);

            final int attLen = attributes.getLength();
            if (attLen > 0) {
                final TextBrick attrParent;
                if (GROUP_ATTRIBUTES) {
                    TextBrick allAttrsBrick =
                            new TextBrick(elementBrick, ALL_ATTRS_TEXT);
                    allAttrsBrick.lineBreak = false;
                    attrParent = allAttrsBrick;
                } else {
                    attrParent = elementBrick;
                }
                for (int i = 0; i < attLen; i++) {
                    final String attName = attributes.getQName(i);
                    final TextBrick attNameBrick =
                            new TextBrick(attrParent, attName + ATTR_SUFFIX);
                    if (i == 0) {
                        attNameBrick.lineBreak = false;
                    }
                    final String attValue = removeCRs(attributes.getValue(i));
                    final TextBrick attValueBrick =
                            new TextBrick(attNameBrick, attValue);
                    attValueBrick.lineBreak = false;
                }
            }
            brick = elementBrick;
        }

        private void handleString() {
            if (buffer.length() != 0) {
                final String str = removeCRs(buffer.toString().trim());
                if (str.length() != 0) {
                    if (brick != null) {
                        @SuppressWarnings("unused")
                        final Brick plainText = new TextBrick(brick, str);
                    }
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
            final TextBrick parent = brick.getParent();
            if (parent != null) {
                brick = parent;
            }
        }
    }

    // ================================================================== Fields

    private final MainWindow mainWindow;

    private String path = "samples/";

    // ============================================================ Constructors

    ImportXMLAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    // ================================================================= Methods

    public void run() {
        final FileDialog dialog =
                new FileDialog(mainWindow.getShell(), SWT.OPEN);
        dialog.setFilterNames(new String[] { "XML files", "All files" });
        dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
        dialog.setFilterPath(path);
        final String fileName = dialog.open();
        if (fileName == null) {
            return;
        }
        path = new File(fileName).getParent();
        try {
            final Editor editor = mainWindow.editor;
            final TextBrick root = parse(fileName);
            editor.setRoot(root);
            mainWindow.setTitle(fileName);
        } catch (Exception e) {
            mainWindow.handleException(e, "Import error");
        }
    }

    private TextBrick parse(String fileName) throws Exception {
        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(false);
        final SAXParser saxParser = saxFactory.newSAXParser();
        final BrickHandler handler = new BrickHandler();
        saxParser.parse(new File(fileName), handler);
        return handler.brick;
    }
}
