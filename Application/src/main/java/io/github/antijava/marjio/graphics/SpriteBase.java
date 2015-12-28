package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.ISprite;
import io.github.antijava.marjio.common.graphics.Viewport;

import java.util.ArrayList;

/**
 * This is a basic {@link ISprite} implement without {@link IBitmap} providing.
 *
 * @author Davy
 */
public abstract class SpriteBase implements ISprite {
    private static ArrayList<ISprite> mSprites = new ArrayList<>();
    private IBitmap mBitmap;
    private Viewport mViewport;
    private int mX = 0, mY = 0, mZ = 0;
    private double mZoomX = 1.0, mZoomY = 1.0;
    private int mOpacity = 0;

    public SpriteBase(final Viewport viewport) {
        mViewport = viewport;
        mSprites.add(this);
    }

    @Override
    public void update() {
    }

    // region Getter
    public ArrayList<ISprite> getSprites(){
        return mSprites;
    }

    @Override
    public IBitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public Viewport getViewport() {
        return mViewport;
    }

    @Override
    public int getX() {
        return mX;
    }

    @Override
    public int getY() {
        return mY;
    }

    @Override
    public int getZ() {
        return mZ;
    }

    @Override
    public double getZoomX() {
        return mZoomX;
    }

    @Override
    public double getZoomY() {
        return mZoomY;
    }

    @Override
    public int getOpacity() {
        return mOpacity;
    }
    // endregion Getter

    // region Setter
    @Override
    public void setBitmap(IBitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public void setViewport(Viewport viewport) {
        mViewport = viewport;
    }

    @Override
    public void setX(int x) {
        mX = x;
    }

    @Override
    public void setY(int y) {
        mY = y;
    }

    @Override
    public void setZ(int z) {
        mZ = z;
    }

    @Override
    public void setZoomX(double zoomX) {
        mZoomX = zoomX;
    }

    @Override
    public void setZoomY(double zoomY) {
        mZoomY = zoomY;
    }

    @Override
    public void setOpacity(int opacity) {
        mOpacity = opacity;
    }
    // endregion Setter
}
