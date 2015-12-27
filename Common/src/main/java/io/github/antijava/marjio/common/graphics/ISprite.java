package io.github.antijava.marjio.common.graphics;

/**
 * Created by fntsr on 2015/12/23.
 */
public interface ISprite {
    /**
     * Updates the sprite for each frame.
     */
    void update();

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

    /**
     * Set viewport this sprite belongs to.
     */
    void setViewport(Viewport viewport);
}
