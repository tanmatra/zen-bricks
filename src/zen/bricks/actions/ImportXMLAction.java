package zen.bricks.actions;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import zen.bricks.Editor;
import zen.bricks.MainWindow;
import zen.bricks.TupleBrick;

public class ImportXMLAction extends Action
{
    // ========================================================== Nested Classes

    private static final class BrickHandler extends DefaultHandler
    {
        private final StringBuilder buffer = new StringBuilder(128);
        TupleBrick brick;
        String groupText;
        String prefix;
        String suffix;
        boolean fillEmpty;

        BrickHandler() { }

        @Override
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
            final StringBuilder res = new StringBuilder(str.length());
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

        @Override
        public void startElement(String uri, String localName,
                String qName, Attributes attributes) throws SAXException
        {
            handleString();
            if (brick != null) {
                brick.newLine();
            }
            final TupleBrick elementBrick = appendBrick(brick, qName);

            final int attLen = attributes.getLength();
            if (attLen > 0) {
                final TupleBrick attrParent;
                if (groupText != null) {
                    final TupleBrick allAttrsBrick =
                            appendBrick(elementBrick, groupText);
                    attrParent = allAttrsBrick;
                } else {
                    attrParent = elementBrick;
                }
                for (int i = 0; i < attLen; i++) {
                    if (i != 0) {
                        attrParent.newLine();
                    }
                    final String attName = attributes.getQName(i);
                    final TupleBrick attNameBrick =
                            appendBrick(attrParent, prefix + attName + suffix);
                    final String attValue = removeCRs(attributes.getValue(i));
                    appendBrick(attNameBrick, attValue);
                }
            }
            brick = elementBrick;
        }

        private void handleString() {
            if (buffer.length() != 0) {
                final String str = removeCRs(buffer.toString().trim());
                if (str.length() != 0) {
                    if (brick != null) {
                        brick.newLine();
                        appendBrick(brick, str);
                    }
                }
                buffer.setLength(0);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException
        {
            buffer.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException
        {
            handleString();
            if ((brick.getContentCount() == 0) && fillEmpty) {
                brick.newLine();
                appendBrick(brick, "");
            }
            final TupleBrick parent = (TupleBrick) brick.getParent();
            if (parent != null) {
                brick = parent;
            }
        }

        private static TupleBrick appendBrick(TupleBrick parent, String text) {
            final TupleBrick result = new TupleBrick(parent, text);
            if (parent != null) {
                parent.appendChild(result);
            }
            return result;
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private static class ImportOptionsDialog extends Dialog
    {
        Button groupCheck;
        Text attrGroupText;
        private final BrickHandler handler;
        private Text prefixText;
        private Text suffixText;
        private Button fillEmptyCheck;

        protected ImportOptionsDialog(Shell parentShell, BrickHandler handler) {
            super(parentShell);
            this.handler = handler;
        }

        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setText("Import XML options");
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            final Composite area = (Composite) super.createDialogArea(parent);
            final GridLayout layout = (GridLayout) area.getLayout();
            layout.numColumns = 2;

            groupCheck = new Button(area, SWT.CHECK);
            groupCheck.setText("&Group attributes under single brick");
            groupCheck.setSelection(true);
            GridDataFactory.fillDefaults().span(2, 1).applyTo(groupCheck);

            final Label attrGroupLabel = new Label(area, SWT.NONE);
            attrGroupLabel.setText("&Attribute brick text:");
            GridDataFactory.swtDefaults().indent(20, 0).applyTo(attrGroupLabel);
            attrGroupText = new Text(area, SWT.SINGLE | SWT.BORDER);
            attrGroupText.setText("@");
            GridDataFactory.swtDefaults().hint(50, SWT.DEFAULT)
                .applyTo(attrGroupText);

            groupCheck.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    final boolean selection = groupCheck.getSelection();
                    attrGroupLabel.setEnabled(selection);
                    attrGroupText.setEnabled(selection);
                }
            });

            new Label(area, SWT.NONE).setText("Attribute &prefix:");
            prefixText = new Text(area, SWT.SINGLE | SWT.BORDER);
            GridDataFactory.swtDefaults().hint(50, SWT.DEFAULT)
                .applyTo(prefixText);

            new Label(area, SWT.NONE).setText("Attribute &suffix:");
            suffixText = new Text(area, SWT.SINGLE | SWT.BORDER);
            suffixText.setText(":");
            GridDataFactory.swtDefaults().hint(50, SWT.DEFAULT)
                .applyTo(suffixText);

            fillEmptyCheck = new Button(area, SWT.CHECK);
            fillEmptyCheck.setText("Add &empty text to empty elements");
            GridDataFactory.swtDefaults().span(2, 1).applyTo(fillEmptyCheck);
            fillEmptyCheck.setSelection(true);

            return area;
        }

        @Override
        protected void okPressed() {
            if (groupCheck.getSelection()) {
                handler.groupText = attrGroupText.getText();
            }
            handler.prefix = prefixText.getText();
            handler.suffix = suffixText.getText();
            handler.fillEmpty = fillEmptyCheck.getSelection();
            super.okPressed();
        }
    }

    // ================================================================== Fields

    private final MainWindow mainWindow;

    private String path = "samples/";

    // ============================================================ Constructors

    public ImportXMLAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    // ================================================================= Methods

    @Override
    public void run() {
        final FileDialog dialog = new FileDialog(mainWindow.getShell(), SWT.OPEN);
        dialog.setFilterNames(new String[] { "XML files", "All files" });
        dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
        dialog.setFilterPath(path);
        final String fileName = dialog.open();
        if (fileName == null) {
            return;
        }

        final BrickHandler handler = new BrickHandler();
        final ImportOptionsDialog optionsDialog =
            new ImportOptionsDialog(mainWindow.getShell(), handler);
        if (optionsDialog.open() != Window.OK) {
            return;
        }

        path = new File(fileName).getParent();
        try {
            final Editor editor = mainWindow.getEditor();
            final TupleBrick root = parse(fileName, handler);
            editor.setDocument(root);
            editor.setFileType(null);
            editor.setFileName(fileName);
        } catch (Exception ex) {
            mainWindow.showException(ex, "Import error");
        }
    }

    private TupleBrick parse(String fileName, BrickHandler handler)
        throws Exception
    {
        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(false);
        final SAXParser saxParser = saxFactory.newSAXParser();
        saxParser.parse(new File(fileName), handler);
        return handler.brick;
    }
}
