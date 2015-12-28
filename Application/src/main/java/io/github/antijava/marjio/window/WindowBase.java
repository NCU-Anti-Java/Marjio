package io.github.antijava.marjio.window;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.graphics.SpriteBase;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Davy on 2015/12/28.
 */
public class WindowBase extends SpriteBase {
    private static final Rectangle BG_STRETCH_SRC = new Rectangle(1,  1, 62, 62);
    private static final Rectangle BG_TILE_SRC = new Rectangle(0, 64, 64, 64);
    private static final Rectangle[] CORNER_SRC = new Rectangle[] {
            new Rectangle( 64,  0, 16, 16),
            new Rectangle(112,  0, 16, 16),
            new Rectangle( 64, 48, 16, 16),
            new Rectangle(112, 48, 16, 16)
    };
    private static final Rectangle[] BORDER_SRC = new Rectangle[] {
            new Rectangle( 64, 16, 16, 32),
            new Rectangle(112, 16, 16, 32),
            new Rectangle( 80,  0, 32, 16),
            new Rectangle( 80, 48, 32, 16)
    };
    private static final Rectangle[] SCROLL_ARROW_SRC = new Rectangle[]{
            new Rectangle( 80, 24, 8, 16),
            new Rectangle(104, 24, 8, 16),
            new Rectangle( 88, 16, 16, 8),
            new Rectangle( 88, 40, 16, 8)
    };
    private static final Rectangle[] PAUSE_SRC = new Rectangle[]{
            new Rectangle( 96, 64, 16, 16),
            new Rectangle(112, 64, 16, 16),
            new Rectangle( 96, 80, 16, 16),
            new Rectangle(112, 80, 16, 16)
    };

    private final IApplication mApplication;
    private IBitmap mWindowskin;
    private final IBitmap mBitmap;
    private final IBitmap mBackgroundBitmap;
    private int mWidth, mHeight;
    private boolean mDirty = true, mSizeChanged = true;

    public WindowBase(@NotNull IApplication application, @NotNull IBitmap windowskin, int width, int height) {
        super(application.getGraphics().getDefaultViewport());
        final IGraphics graphics = application.getGraphics();

        mApplication = application;
        mWindowskin = windowskin;
        mBitmap = graphics.createBitmap(width, height);
        mBackgroundBitmap = graphics.createBitmap(width, height);
        setWidth(width);
        setHeight(height);
    }

    // region Getter
    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    @Override
    public IBitmap getBitmap() {
        return mBitmap;
    }
    // endregion Getter

    // region Setter
    public void setWidth(int width) {
        if (width < 32)
            width = 32;

        if (width != mWidth) {
            mWidth = width;
            mSizeChanged = true;
        }
    }

    public void setHeight(int height) {
        if (height < 32)
            height = 32;

        if (height != mHeight) {
            mHeight = height;
            mSizeChanged = true;
        }
    }
    // endregion Setter

    @Override
    public void update() {
        super.update();

        if (mSizeChanged)
            sizeChanged();

        if (mDirty)
            mBitmap.blt(0, 0, mBackgroundBitmap, mBackgroundBitmap.getRect(), 0);
    }

    private void rebuildBackground() {
        mBackgroundBitmap.clear();

        if (mWidth == 0 || mHeight == 0)
            return;

        // Stretched background
        mBackgroundBitmap.stretchBlt(4, 4, mWidth - 4, mHeight - 4, mWindowskin, BG_STRETCH_SRC, 0);

        // Tiled background
        mBackgroundBitmap.tileBlt(4, 4, mWidth - 4, mHeight - 4, mWindowskin, BG_TILE_SRC, 0);

        // Corners
        mBackgroundBitmap.stretchBlt(0, 0, 16, 16, mWindowskin, CORNER_SRC[0], 0);
        mBackgroundBitmap.stretchBlt(mWidth - 16, 0, 16, 16, mWindowskin, CORNER_SRC[1], 0);
        mBackgroundBitmap.stretchBlt(0, mHeight - 16, 16, 16, mWindowskin, CORNER_SRC[2], 0);
        mBackgroundBitmap.stretchBlt(mWidth - 16, mHeight - 16, 16, 16, mWindowskin, CORNER_SRC[3], 0);

        // Sides
        mBackgroundBitmap.tileBlt(0, 16, 16, mHeight - 32, mWindowskin, BORDER_SRC[0], 0);
        mBackgroundBitmap.tileBlt(mWidth - 16, 16, 16, mHeight - 32, mWindowskin, BORDER_SRC[1], 0);
        mBackgroundBitmap.tileBlt(16, 0, mWidth - 32, 16, mWindowskin, BORDER_SRC[2], 0);
        mBackgroundBitmap.tileBlt(16, mHeight - 16, mWidth - 32, 16, mWindowskin, BORDER_SRC[3], 0);
    }

    private void sizeChanged() {
        mBitmap.clear();
        mBackgroundBitmap.clear();
        mBitmap.resize(mWidth, mHeight);
        mBackgroundBitmap.resize(mWidth, mHeight);

        rebuildBackground();
        mSizeChanged = false;
        mDirty = true;
    }
}
