package io.github.antijava.marjio.common.graphics;

import java.util.ArrayList;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface ISprite {
    /**
     * Updates the sprite for each frame.
     */
    void update();

    // region Getter
    /**
     * Returns the image for the sprite.
     */
    IBitmap getBitmap();

    /**
     * Returns viewport which this sprite belongs to.
     */
    Viewport getViewport();

    /**
     * Returns x coordinate for the upper-left corner.
     */
    int getX();

    /**
     * Returns y coordinate for the upper-left corner.
     */
    int getY();

    /**
     * Returns z coordinate.
     */
    int getZ();

    /**
     * Returns x scale for zooming.
     * Defaults to 1.
     */
    double getZoomX();

    /**
     * Returns y scale for zooming.
     * Defaults to 1.
     */
    double getZoomY();

    /**
     * Returns opacity.
     * Range 0 to 255.
     */
    int getOpacity();
    // endregion Getter

    // region Setter

    /**
     * Sets the image for the sprite.
     *
     * @param bitmap The bitmap.
     */
    void setBitmap(IBitmap bitmap);

    /**
     * Sets viewport this sprite belongs to.
     *
     * @param viewport The viewport.
     */
    void setViewport(Viewport viewport);

    /**
     * Sets x coordinate for the upper-left corner.
     *
     * @param x The x coordinate.
     */
    void setX(int x);

    /**
     * Sets y coordinate for the upper-left corner.
     *
     * @param y The y coordinate.
     */
    void setY(int y);

    /**
     * Sets z coordinate.
     *
     * @param z The z coordinate.
     */
    void setZ(int z);

    /**
     * Sets x scale for zooming.
     *
     * @param zoomX The x scale for zooming.
     */
    void setZoomX(double zoomX);

    /**
     * Returns y scale for zooming.
     *
     * @param zoomY The y scale for zooming.
     */
    void setZoomY(double zoomY);

    /**
     * Sets opacity.
     *
     * @param opacity The opacity, must be in [0, 255].
     */
    void setOpacity(int opacity);
    // endregion Setter
}
