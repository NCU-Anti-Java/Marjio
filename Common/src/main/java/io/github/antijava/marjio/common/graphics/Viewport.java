package io.github.antijava.marjio.common.graphics;

/**
 * Created by fntsr on 2015/12/23.
 */
public class Viewport {
    static public final int MATCH_WINDOW = -1;
    public int x, y, z, ox, oy, width, height;

    public Viewport() {
        this(0, 0);
    }

    public Viewport(int _x, int _y) {
        this(_x, _y, 0);
    }

    public Viewport(int _x, int _y, int _z) {
        this(_x, _y, _z, MATCH_WINDOW, MATCH_WINDOW);
    }

    public Viewport(int _x, int _y, int _width, int _height) {
        this(_x, _y, 0, _width, _height);
    }

    public Viewport(int _x, int _y, int _z, int _width, int _height) {
        x = _x;
        y = _y;
        z = _z;
        width = _width;
        height = _height;
        ox = 0;
        oy = 0;
    }

    /**
     * Returns the rectangle of the viewport.
     */
    Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Sets the rectangle of the viewport.
     */
    void setRect(Rectangle rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }
}
