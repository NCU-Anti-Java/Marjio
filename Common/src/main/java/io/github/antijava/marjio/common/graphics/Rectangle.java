package io.github.antijava.marjio.common.graphics;

/**
 * Created by Davy on 2015/12/25.
 */
public class Rectangle {
    public int x, y, width, height;

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(final Rectangle rect) { this(rect.x, rect.y, rect.width, rect.height); }

    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
