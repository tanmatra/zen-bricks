package zen.bricks;

import org.eclipse.swt.graphics.GC;

public abstract class Brick
{
    int index;
    Brick parent;
    int x;
    int y;
    int width;
    int height;

    Brick(Brick parent) {
        this.parent = parent;
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
//        gc.drawRectangle(baseX + x, baseY + y, width - 1, height - 1);
        gc.drawRoundRectangle(baseX, baseY, width - 1, height - 1,
                6, 6);
    }

    void paint(GC gc, int baseX, int baseY, UI ui) {
        paintBackground(gc, baseX, baseY, ui);
        paintBorder(gc, baseX, baseY, ui);
    }

    void childResized(Brick child) {
        // todo
    }

    void calculateSize(UI ui) {
        // todo
    }
}
