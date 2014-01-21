package zen.bricks.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMPreferences extends StoredPreferences
{
    // ============================================================ Class Fields

    public static Preferences load(File file) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.parse(file);
        return new DOMPreferences(document);
    }

    // ================================================================== Fields

    private Element element;

    // ============================================================ Constructors

    public DOMPreferences() {
        super(null, "");
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        final Document document = builder.newDocument();
        element = document.createElement("theme");
        document.appendChild(element);
    }

    public DOMPreferences(Document document) {
        super(null, "");
        this.element = document.getDocumentElement();
    }

    protected DOMPreferences(DOMPreferences parent, String name, Element element) {
        super(parent, name);
        this.element = element; // FIXME
    }

    // ================================================================= Methods

    @Override
    protected void putSpi(String key, String value) {
        element.setAttribute(key, value);
    }

    @Override
    protected String getSpi(String key) {
        return element.getAttribute(key);
    }

    @Override
    protected void removeSpi(String key) {
        element.removeAttribute(key);
    }

    @Override
    protected void removeNodeSpi() throws BackingStoreException {
        final Node parentNode = element.getParentNode();
        parentNode.removeChild(element);
    }

    private Element findByName(String name) {
        final NodeList nodes = element.getChildNodes();
        final int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
            final Node item = nodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element element = (Element) item;
            if (element.getTagName().equals(name)) {
                return element;
            }
        }
        return null;
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        final NamedNodeMap attributes = element.getAttributes();
        final List<String> list = new ArrayList<String>();
        final int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            final Attr attr = (Attr) attributes.item(i);
            list.add(attr.getName());
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
        final NodeList nodes = element.getChildNodes();
        final List<String> list = new ArrayList<String>();
        final int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
            final Node item = nodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            final Element element = (Element) item;
            list.add(element.getTagName());
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    protected AbstractPreferences childSpi(String name) {
        Element child = findByName(name);
        if (child == null) {
            child = element.getOwnerDocument().createElement(name);
            element.appendChild(child);
        }
        return new DOMPreferences(this, name, child);
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
        // TODO Auto-generated method stub
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
        // TODO Auto-generated method stub
    }

    @Override
    public void save(String fileName) throws IOException {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final Transformer transformer;
        try {
            transformer = factory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IOException(e);
        }
        final DOMSource source = new DOMSource(element.getOwnerDocument());
        final StreamResult target = new StreamResult(new File(fileName));
        try {
            transformer.transform(source, target);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }
}
