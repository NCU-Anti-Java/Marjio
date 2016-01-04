package io.github.antijava.marjio.common.graphics;

/**
 * A color class, holding the r, g, b and alpha component as ints in the range [0, 255].
 * All methods perform clamping on the internal values after execution.
 *
 * @author Jason
 */
public class Color {
    public static final Color BLACK = new Color(0xff000000);
    public static final Color WHITE = new Color(0xffffffff);
    public static final Color RED = new Color(0xff0000ff);

    /** The red, green, blue and alpha components **/
    public int r, g, b, a;

    // region Constructor
    /**
     * @see #argbToColor(Color, int)
     */
    public Color(int argb) {
        set(argb);
    }

    /**
     * Constructor, sets the components of the color
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     */
    public Color(int r, int g, int b, int a) {
        set(r, g, b, a);
    }
    // endregion Constructor

    // region Set
    /**
     * Sets this color's component values through an integer representation.
     *
     * @return this Color for chaining
     * @see #argbToColor(Color, int)
     */
    public Color set(int argb) {
        argbToColor(this, argb);
        return this;
    }

    /**
     * Sets this Color's component values.
     *
     * @param r Red component
     * @param g Green component
     * @param b Blue component
     * @param a Alpha component
     * @return this Color for chaining
     */
    public Color set(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return clamp();
    }
    // endregion Set

    /**
     * Clamps this Color's components to a valid range [0 - 255]
     *
     * @return this Color for chaining
     */
    public Color clamp() {
        if (r < 0)
            r = 0;
        else if (r > 255) r = 255;

        if (g < 0)
            g = 0;
        else if (g > 255) g = 255;

        if (b < 0)
            b = 0;
        else if (b > 255) b = 255;

        if (a < 0)
            a = 0;
        else if (a > 255) a = 255;

        return this;
    }

    /**
     * Packs the color components into a 32-bit integer with the format ARGB.
     *
     * @return the packed color as a 32-bit int.
     */
    public int toIntBits() {
        return a << 24 | r << 16 | g << 8 | b;
    }

    /**
     * Sets the Color components using the specified integer value in the format ARGB.
     *
     * @param color The Color to be modified.
     * @param value An integer color value in ARGB format.
     */
    public static void argbToColor(Color color, int value) {
        color.a = (value & 0xff000000) >>> 24;
        color.r = (value & 0x00ff0000) >>> 16;
        color.g = (value & 0x0000ff00) >>> 8;
        color.b = value & 0x000000ff;
    }

    // region Override
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color)o;
        return toIntBits() == color.toIntBits();
    }

    @Override
    public int hashCode() {
        int result = a;
        result = 31 * result + r;
        result = 31 * result + g;
        result = 31 * result + b;
        return result;
    }

    /**
     * Returns the color encoded as hex string with the format AARRGGBB.
     */
    @Override
    public String toString() {
        String value = Integer.toHexString(a << 24 | r << 16 | g << 8 | b);
        while (value.length() < 8)
            value = "0" + value;
        return value;
    }
    // endregion Override
}
