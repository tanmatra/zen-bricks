package zen.bricks;

import org.eclipse.swt.graphics.GC;

public abstract class Brick
{
    private static int BORDER_ARC_SIZE = 0; // 6

    TextBrick parent;
    int index;
    int x;
    int y;
    int width;
    int height;
    boolean lineBreak = true;

    Brick(TextBrick parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public TextBrick getParent() {
        return parent;
    }

    void realize(UI ui) {
    }

    void dispose() {
    }

    private void paintBackground(GC gc, int baseX, int baseY, UI ui) {
        gc.setBackground(ui.getBackgroundColor());
        gc.fillRectangle(baseX, baseY, width, height);
    }

    private void paintBorder(GC gc, int baseX, int baseY, UI ui) {
        gc.setForeground(ui.getBorderColor());
        if (BORDER_ARC_SIZE == 0) {
            gc.drawRectangle(baseX, baseY, width - 1, height - 1);
        } else {
            gc.drawRoundRectangle(baseX, baseY, width - 1, height - 1,
                    BORDER_ARC_SIZE, BORDER_ARC_SIZE);
        }
    }

    void paint(GC gc, int baseX, int baseY, UI ui) {
        paintBackground(gc, baseX, baseY, ui);
        paintBorder(gc, baseX, baseY, ui);
    }

    void calculateSize(UI ui) {
        // todo
    }

    boolean isLineBreak() {
        return lineBreak;
    }

    int getAscent(UI ui) {
        return 0;
    }
}
