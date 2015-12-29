package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.exception.ObjectDisposedException;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.ISprite;
import io.github.antijava.marjio.common.graphics.Viewport;

import java.util.ArrayList;

/**
 * This is a basic {@link ISprite} implement without {@link IBitmap} providing.
 *
 * @author Davy
 */
public abstract class SpriteBase extends Sprite {
    private IBitmap mBitmap;
    private int mX = 0, mY = 0, mZ = 0;
    private double mZoomX = 1.0, mZoomY = 1.0;
    private int mOpacity = 0;

    public SpriteBase(final Viewport viewport) {
        setViewport(viewport);
    }

    @Override
    public void update() {
    }

    @Override
    public void dispose() {
        super.dispose();

        mBitmap = null;
    }

    // region Getter
    @Override
    public IBitmap getBitmap() {
        if (isDisposed())
            throw new ObjectDisposedException();
        return mBitmap;
    }

    @Override
    public int getX() {
        if (isDisposed())
            throw new ObjectDisposedException();
        return mX;
    }

    @Override
    public int getY() {
        if (isDisposed())
            throw new ObjectDisposedException();
        return mY;
    }

    @Override
    public int getZ() {
        if (isDisposed())
            throw new ObjectDisposedException();
        return mZ;
    }

    @Override
    public double getZoomX() {
        if (isDisposed())
            throw new ObjectDisposedException();
        return mZoomX;
    }

    @Override
    public double getZoomY() {
        if (isDisposed())
            throw new ObjectDisposedException();
        return mZoomY;
    }

    @Override
    public int getOpacity() {
        if (isDisposed())
            throw new ObjectDisposedException();
        return mOpacity;
    }
    // endregion Getter

    // region Setter
    @Override
    public void setBitmap(IBitmap bitmap) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mBitmap = bitmap;
    }

    @Override
    public void setX(int x) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mX = x;
    }

    @Override
    public void setY(int y) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mY = y;
    }

    @Override
    public void setZ(int z) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mZ = z;
    }

    @Override
    public void setZoomX(double zoomX) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mZoomX = zoomX;
    }

    @Override
    public void setZoomY(double zoomY) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mZoomY = zoomY;
    }

    @Override
    public void setOpacity(int opacity) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mOpacity = opacity;
    }
    // endregion Setter
}
