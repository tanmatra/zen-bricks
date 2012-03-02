package zen.bricks;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
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

    public void attach(Editor editor) {
        super.attach(editor);
        textLayout = new TextLayout(editor.getUI().getDevice());
    }

    public void detach(Editor editor) {
        if (textLayout != null) {
            textLayout.dispose();
            textLayout = null;
        }
        super.detach(editor);
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

        final Margin padding = chain.get(TupleStyle.TEXT_PADDING);
        setTextX(padding.getLeft());
        setTextY(padding.getTop());

        final Font font = chain.find(TupleStyle.FONT).getFont();
        textLayout.setFont(font);

        final Color foreground =
                chain.find(TupleStyle.FOREGROUND).getForegroundColor();
        final Color background =
                chain.find(TupleStyle.TEXT_BACKGROUND).getTextBackground()
                        .getColor();
        final TextStyle style = new TextStyle(null, foreground, background);
        textLayout.setStyle(style, 0, text.length() - 1);

        final Rectangle bounds = textLayout.getBounds();
        setWidth(bounds.width + padding.getHorizontalSum());
        setHeight(bounds.height + padding.getVerticalSum());

        final FontMetrics lineMetrics = textLayout.getLineMetrics(0);
        setAscent(lineMetrics.getAscent() + padding.getTop());

        valid = true;
    }

    protected void doPaint(GC gc, int selfX, int selfY, Editor editor) {
//        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
//        gc.fillRectangle(selfX, selfY, getWidth(), getHeight());

        editor.getUI().prepareTextPaint(gc);
        textLayout.draw(gc, selfX + getTextX(), selfY + getTextY());
    }
}
