package io.github.antijava.marjio.common.graphics;

import io.github.antijava.marjio.common.Disposable;
import io.github.antijava.marjio.common.IGraphics;

/**
 * Created by Davy on 2015/12/25.
 */
public interface IBitmap extends Disposable {
    // region Drawing
    /**
     * Clears the entire bitmap.
     */
    void clear();

    /**
     * Clears the specified rectangle
     *
     * @param x The x coordinate for the upper-left corner.
     * @param y The y coordinate for the upper-left corner.
     * @param width The width of the rectangle to clear.
     * @param height The height of the rectangle to clear.
     */
    void clearRect(int x, int y, int width, int height);
    /**
     * Clears the specified rectangle
     *
     * @param rect The rectangle.
     */
    void clearRect(Rectangle rect);

    /**
     * Draws the outline text to the bitmap.
     *
     * @param text The text that will be drawn.
     * @param x The x coordinate for the left of the text.
     * @param y The y coordinate for the top of the text.
     * @param maxWidth The maximum allowed width of the text.
     * @param lineHeight The height of the text line.
     * @param font The font of the text.
     * @param align The alignment of the text.
     */
    void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, IFont font, TextAlign align);

    /**
     * {@code align} defaults to {@link TextAlign#LEFT}.
     *
     * @see #drawText(CharSequence, int, int, int, int, IFont, TextAlign)
     */
    void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, IFont font);
    /**
     * {@code font} defaults to {@link IGraphics#getDefaultFont()}.
     *
     * @see #drawText(CharSequence, int, int, int, int, IFont, TextAlign)
     */
    void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, TextAlign align);
    /**
     * {@code align} defaults to {@link TextAlign#LEFT}.
     * {@code font} defaults to {@link IGraphics#getDefaultFont()}.
     *
     * @see #drawText(CharSequence, int, int, int, int, IFont, TextAlign)
     */
    void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight);

    /**
     * Fills the entire bitmap.
     *
     * @param color The color of the rectangle.
     */
    void fillAll(Color color);

    /**
     * Fills the specified rectangle.
     *
     * @param x The x coordinate for the upper-left corner.
     * @param y The y coordinate for the upper-left corner
     * @param width The width of the rectangle to fill.
     * @param height The height of the rectangle to fill.
     * @param color The color of the rectangle.
     */
    void fillRect(int x, int y, int width, int height, Color color);

    /**
     * Fills the specified rectangle.
     *
     * @param rect The rectangle to fill.
     * @param color The color of the rectangle.
     */
    void fillRect(Rectangle rect, Color color);

    /**
     * Returns pixel color at the specified point.
     *
     * @param x The x coordinate of the pixel in the bitmap.
     * @param y The y coordinate of the pixel in the bitmap.
     *
     * @return The pixel color.
     */
    Color getPixel(int x, int y);

    /**
     * Resizes the bitmap.
     *
     * @param width The new width of the bitmap.
     * @param height The new height of the bitmap.
     */
    void resize(int width, int height);
    // endregion Drawing

    // region Getter
    /**
     * Returns the height of the bitmap.
     */
    int getHeight();

    /**
     * Returns the width of the bitmap.
     */
    int getWidth();

    /**
     * Returns the rectangle of the bitmap.
     */
    Rectangle getRect();
    // endregion Getter

    enum TextAlign {
        LEFT,
        CENTER,
        RIGHT,
    }
}
