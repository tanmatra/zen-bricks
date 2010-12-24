package zen.bricks;

import java.util.StringTokenizer;

public class Margin
{
    int left;
    int top;
    int right;
    int bottom;

    public Margin() {
    }

    public Margin(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public static Margin parseMargin(String string) {
        if (string == null) {
            return null;
        }
        final StringTokenizer tokenizer = new StringTokenizer(string, ", ");
        final Margin margin = new Margin();
        margin.left   = Integer.parseInt(tokenizer.nextToken());
        margin.top    = Integer.parseInt(tokenizer.nextToken());
        margin.right  = Integer.parseInt(tokenizer.nextToken());
        margin.bottom = Integer.parseInt(tokenizer.nextToken());
        return margin;
    }

    public String format() {
        return left + ", " + top + ", " + right + ", " + bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getHorizontalSum() {
        return left + right;
    }

    public int getVerticalSum() {
        return top + bottom;
    }
}
