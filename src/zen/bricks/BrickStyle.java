package zen.bricks;

import zen.bricks.styleeditor.IBrickStyleEditor;

public abstract class BrickStyle
{
    private final String name;

    public BrickStyle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract IBrickStyleEditor createEditor(UI ui);

    public abstract void dispose();
}
