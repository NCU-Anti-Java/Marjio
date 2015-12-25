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
     * Returns z coordinate for the upper-left corner.
     */
    int getZ();

    /**
     * Returns x coordinate for the upper-left corner.
     */
    int getZoomX();

    /**
     * Returns x coordinate for the upper-left corner.
     */
    int getZoomY();

    /**
     * Returns x coordinate for the upper-left corner.
     */
    int getOpacity();

    /**
     * Set viewport this sprite belongs to.
     */
    void setViewport(Viewport viewport);
}
