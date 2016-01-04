package io.github.antijava.marjio.common.graphics;

import io.github.antijava.marjio.common.Disposable;
import io.github.antijava.marjio.common.IGraphics;

/**
 * @author Davy
 * @author Jason
 */
public interface IBitmap extends Disposable {
    // region Drawing
    /**
     * Clears the entire bitmap.
     */
    void clear();

    /**
     * Returns a rectangle contains the outline text.
     *
     * @param text The text.
     * @param lineHeight The height of the text line.
     *
     * @return The rectangle measured.
     */
    Rectangle measureText(CharSequence text, int lineHeight);

    /**
     * Resizes the bitmap.
     *
     * @param width The new width of the bitmap.
     * @param height The new height of the bitmap.
     */
    void resize(int width, int height);

    /**
     * Blur the bitmap.
     */
    void blur();
    // endregion Drawing

    // region Draw Text
    /**
     * Draws the outline text on the bitmap.
     *
     * @param text The text that will be drawn.
     * @param color The color of the text.
     * @param x The x coordinate for the left of the text.
     * @param y The y coordinate for the top of the text.
     * @param maxWidth The maximum allowed width of the text. -1 for unlimited.
     * @param lineHeight The height of the text line.
     * @param align The alignment of the text.
     */
    void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, Color color, TextAlign align);

    /**
     * {@code color} defaults to {@link Color::BLACK}.
     *
     * @see #drawText(CharSequence, int, int, int, int, Color, TextAlign)
     */
    void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, TextAlign align);

    /**
     * {@code align} defaults to {@link TextAlign#LEFT}.
     *
     * @see #drawText(CharSequence, int, int, int, int, Color, TextAlign)
     */
    void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, Color color);

    /**
     * {@code color} defaults to {@link Color::BLACK}.
     * {@code align} defaults to {@link TextAlign#LEFT}.
     *
     * @see #drawText(CharSequence, int, int, int, int, Color, TextAlign)
     */
    void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight);

    /**
     * Draws the outline text on the bitmap.
     *
     * @param text The text that will be drawn.
     * @param color The color of the text.
     * @param rect The box for the text.
     * @param align The alignment of the text.
     */
    void drawText(CharSequence text, Rectangle rect, Color color, TextAlign align);

    /**
     * {@code color} defaults to {@link Color::BLACK}.
     *
     * @see #drawText(CharSequence, Rectangle, Color, TextAlign)
     */
    void drawText(CharSequence text, Rectangle rect, TextAlign align);

    /**
     * {@code align} defaults to {@link TextAlign#LEFT}.
     *
     * @see #drawText(CharSequence, Rectangle, Color, TextAlign)
     */
    void drawText(CharSequence text, Rectangle rect, Color color);

    /**
     * {@code color} defaults to {@link Color::BLACK}.
     * {@code align} defaults to {@link TextAlign#LEFT}.
     *
     * @see #drawText(CharSequence, Rectangle, Color, TextAlign)
     */
    void drawText(CharSequence text, Rectangle rect, int lineHeight);
    // endregion Draw Text

    // region Clear Rectangle
    /**
     * Clears the specified rectangle area of the bitmap.
     *
     * @param x The x coordinate for the upper-left corner.
     * @param y The y coordinate for the upper-left corner.
     * @param width The width of the rectangle to clear.
     * @param height The height of the rectangle to clear.
     */
    void clearRect(int x, int y, int width, int height);

    /**
     * Clears the specified rectangle of the bitmap.
     *
     * @param rect The rectangle.
     */
    void clearRect(Rectangle rect);
    // endregion Clear Rectangle

    // region Fill Rectangle
    /**
     * Fills the entire bitmap with specified color.
     *
     * @param color The color of the rectangle.
     */
    void fillAll(Color color);

    /**
     * Fills the specified rectangle with specified color.
     *
     * @param x The x coordinate for the upper-left corner.
     * @param y The y coordinate for the upper-left corner
     * @param width The width of the rectangle to fill.
     * @param height The height of the rectangle to fill.
     * @param color The color of the rectangle.
     */
    void fillRect(int x, int y, int width, int height, Color color);

    /**
     * Fills the specified rectangle with specified color.
     *
     * @param rect The rectangle to fill.
     * @param color The color of the rectangle.
     */
    void fillRect(Rectangle rect, Color color);
    // endregion Fill Rectangle

    // region Block transfer
    /**
     * Performs a block transfer from the {@param src} box {@param srcRect} to specified coordinates.
     *
     * @param x The x coordinate for the left-top corner.
     * @param y The y coordinate for the left-top corner.
     * @param src The source bitmap.
     * @param srcRect The source rectangle for the bitmap.
     * @param opacity Opacity can be set from 0 to 255.
     */
    void blt(int x, int y, IBitmap src, Rectangle srcRect, int opacity);

    /**
     * Performs a block transfer from the {@param src} box {@param srcRect} to destination rect.
     *
     * @param x The x coordinate for the left-top corner of destination rectangle.
     * @param y The y coordinate for the left-top corner of destination rectangle.
     * @param width The width of destination rectangle.
     * @param height The height of destination rectangle.
     * @param src The source bitmap.
     * @param srcRect The source rectangle for the bitmap.
     * @param opacity Opacity can be set from 0 to 255.
     */
    void stretchBlt(int x, int y, int width, int height, IBitmap src, Rectangle srcRect, int opacity);

    /**
     * Performs a block transfer from the {@param src} box {@param srcRect} to destination rect.
     *
     * @param rect The destination rectangle.
     * @param src The source bitmap.
     * @param srcRect The source rectangle for the bitmap.
     * @param opacity Opacity can be set from 0 to 255.
     */
    void stretchBlt(Rectangle rect, IBitmap src, Rectangle srcRect, int opacity);

    /**
     * Performs a block tiled from the {@param src} box {@param srcRect} to destination rect.
     *
     * @param x The x coordinate for the left-top corner of destination rectangle.
     * @param y The y coordinate for the left-top corner of destination rectangle.
     * @param width The width of destination rectangle.
     * @param height The height of destination rectangle.
     * @param src The source bitmap.
     * @param srcRect The source rectangle for the bitmap.
     * @param opacity Opacity can be set from 0 to 255.
     */
    void tileBlt(int x, int y, int width, int height, IBitmap src, Rectangle srcRect, int opacity);

    /**
     * Performs a block tiled from the {@param src} box {@param srcRect} to destination rect.
     *
     * @param rect The destination rectangle.
     * @param src The source bitmap.
     * @param srcRect The source rectangle for the bitmap.
     * @param opacity Opacity can be set from 0 to 255.
     */
    void tileBlt(Rectangle rect, IBitmap src, Rectangle srcRect, int opacity);
    // endregion Block Transfer

    // region Setter
    /**
     * Set the font use to {@link #drawText} and {@link #measureText}.
     *
     * @param font The font.
     */
    void setFont(IFont font);

    /**
     * Changes pixel color to the specified point.
     *
     * @param x The x coordinate of the pixel in the bitmap.
     * @param y The y coordinate of the pixel in the bitmap.
     * @param color The pixel color.
     */
    void setPixel(int x, int y, Color color);
    // endregion Setter

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
     * Returns the font set to {@link #drawText} and {@link #measureText}.
     * Defaults to {@link IGraphics#getDefaultFont()}.
     */
    IFont getFont();

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
     * Returns the rectangle of the bitmap.
     */
    Rectangle getRect();
    // endregion Getter

    // region Enum
    enum TextAlign {
        LEFT,
        CENTER,
        RIGHT,
    }
    // endregion Enum
}
