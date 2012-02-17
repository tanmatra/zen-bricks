package zen.bricks;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;

public class ScriptLabelRenderer extends LabelRenderer
{
    private TextLayout textLayout;

    private boolean valid;

    protected ScriptLabelRenderer(TupleBrick tupleBrick) {
        super(tupleBrick);
    }

    public void init(Editor editor) {
        super.init(editor);
        textLayout = new TextLayout(editor.getUI().getDevice());
    }

    public void dispose() {
        if (textLayout != null) {
            textLayout.dispose();
            textLayout = null;
        }
        super.dispose();
    }

    public void invalidate() {
        valid = false;
    }

    public void doLayout(Editor editor) {
        if (valid) {
            return;
        }

        final String text = getText();
        textLayout.setText(text);
        final StyleChain chain = editor.getStyleChain(getTupleBrick());
        final TupleStyle fontStyle = chain.find(TupleStyle.FONT);
        final Font font = fontStyle.getFont();
        textLayout.setFont(font);
        final Color foreground =
                chain.find(TupleStyle.FOREGROUND).getForegroundColor();
        final Color background =
                chain.find(TupleStyle.TEXT_BACKGROUND).getTextBackground()
                        .getColor();
        final TextStyle style = new TextStyle(null, foreground, background);
        textLayout.setStyle(style, 0, text.length() - 1);
        final Rectangle bounds = textLayout.getBounds();
        textExtent = new Point(bounds.width, bounds.height);

        valid = true;
    }

    public void paint(GC gc, int baseX, int baseY, Rectangle clipping,
            Editor editor)
    {
        final int textX = baseX + getX();
        final int textY = baseY + getY();
        if (!clipping.intersects(textX, textY, getWidth(), getHeight())) {
            return;
        }
        editor.getUI().prepareTextPaint(gc);
        textLayout.draw(gc, textX, textY);
    }
}
