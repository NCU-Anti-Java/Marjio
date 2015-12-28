package io.github.antijava.marjio.window;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.constant.WindowConstant;
import io.github.antijava.marjio.graphics.SpriteBase;
import org.jetbrains.annotations.NotNull;

import java.nio.file.NoSuchFileException;

/**
 * Created by Davy on 2015/12/28.
 */
public class WindowBase extends SpriteBase implements WindowConstant {
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
    private static final Rectangle[] SCROLL_ARROW_SRC = new Rectangle[] {
            new Rectangle( 80, 24, 8, 16),
            new Rectangle(104, 24, 8, 16),
            new Rectangle( 88, 16, 16, 8),
            new Rectangle( 88, 40, 16, 8)
    };
    private static final Rectangle[] PAUSE_SRC = new Rectangle[] {
            new Rectangle( 96, 64, 16, 16),
            new Rectangle(112, 64, 16, 16),
            new Rectangle( 96, 80, 16, 16),
            new Rectangle(112, 80, 16, 16)
    };
    private static final Rectangle[] CURSOR_CORNER_SRC = new Rectangle[] {
            new Rectangle(64, 64, 4, 4),
            new Rectangle(92, 64, 4, 4),
            new Rectangle(64, 92, 4, 4),
            new Rectangle(92, 92, 4, 4)
    };
    private static final Rectangle[] CURSOR_BORDER_SRC = new Rectangle[] {
            new Rectangle(64, 68,  4, 24),
            new Rectangle(92, 68,  4, 24),
            new Rectangle(68, 64, 24,  4),
            new Rectangle(68, 92, 24,  4)
    };
    private static final Rectangle CURSOR_BG_SRC = new Rectangle(68, 68, 24, 24);
    private static final char[] CURSOR_ANIMATION_SRC = {
        	/* Fade out */
            0xFF, 0xF7, 0xEF, 0xE7, 0xDF, 0xD7, 0xCF, 0xC7,
            0xBF, 0xB7, 0xAF, 0xA7, 0x9F, 0x97, 0x8F, 0x87,
	        /* Fade in */
            0x7F, 0x87, 0x8F, 0x97, 0x9F, 0xA7, 0xAF, 0xB7,
            0xBF, 0xC7, 0xCF, 0xD7, 0xDF, 0xE7, 0xEF, 0xF7
    };

    private final IApplication mApplication;
    private IBitmap mWindowskin;
    private IBitmap mBitmap;
    private IBitmap mBackgroundBitmap;
    private IBitmap mCursorBitmap;
    private int mWidth, mHeight;
    private Rectangle mCursorRect;
    private int mCursorAnimationIdx = 0;
    private boolean mActive = false;
    private boolean mDirty = true,
            mBackgroundDirty = true,
            mCursorDirty = true;
    private boolean mSizeChanged = true;

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

    public WindowBase(@NotNull IApplication application, final int width, final int height) {
        this(application, getDefaultWindowskin(application), width, height);
    }

    // region Getter
    protected IApplication getApplication() {
        return mApplication;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Rectangle getCursorRect() {
        return mCursorRect;
    }

    public boolean isActive() {
        return mActive;
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

    public void setActive(boolean active) {
        if (mActive == active)
            return;

        mActive = active;
        mCursorDirty = true;
    }

    public void setCursorRect(final Rectangle rect) {
        if (rect.width < 16)
            rect.width = 16;
        if (rect.height < 16)
            rect.height = 16;

        // TODO: compare rectangle's x, y, w, h.
        if (mCursorRect == rect)
            return;

        if (mCursorBitmap != null) {
            mCursorBitmap.dispose();
            mCursorBitmap = null;
        }
        mCursorRect = rect;
        mCursorDirty = true;
    }
    // endregion Setter

    @Override
    public void update() {
        super.update();

        if (mSizeChanged) {
            if (mBackgroundBitmap != null) {
                mBackgroundBitmap.dispose();
                mBackgroundBitmap = null;
                mBackgroundDirty = true;
            }

            if (mBitmap != null) {
                mBitmap.dispose();
                mBitmap = null;
            }
        }

        if (mBitmap == null) {
            mBitmap = mApplication.getGraphics().createBitmap(getWidth(), getHeight());
            mDirty = true;
        }

        if (mBackgroundDirty)
            rebuildBackground();

        if (mCursorDirty)
            rebuildCursor();

        if (!mDirty)
            return;

        mBitmap.blt(0, 0, mBackgroundBitmap, mBackgroundBitmap.getRect(), 0);

        if (isActive()) {
            mCursorAnimationIdx = (mCursorAnimationIdx + 1) % CURSOR_ANIMATION_SRC.length;

            // TODO: convert list to (255 - it)
            final int cursorOpacity = 255 - CURSOR_ANIMATION_SRC[mCursorAnimationIdx];
            mBitmap.blt(getCursorRect().x + 16, getCursorRect().y + 16, mCursorBitmap, mCursorBitmap.getRect(), cursorOpacity);
        }
    }

    private void rebuildBackground() {
        mBackgroundDirty = false;

        if (mBackgroundBitmap == null)
            mBackgroundBitmap = mApplication.getGraphics().createBitmap(getWidth(), getHeight());
        mBackgroundBitmap.clear();

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

        mDirty = true;
    }

    private void rebuildCursor() {
        mCursorDirty = false;

        if (mCursorRect == null || mCursorRect.width < 0 || mCursorRect.height < 0)
            return;

        if (mCursorBitmap == null)
            mCursorBitmap = mApplication.getGraphics().createBitmap(mCursorRect.width, mCursorRect.height);
        mCursorBitmap.clear();

        final Rectangle cursorRect = getCursorRect();

        // Corner
        final Rectangle cornerTop = new Rectangle(cursorRect);
        mCursorBitmap.blt(cornerTop.x, cornerTop.y, mWindowskin, CURSOR_CORNER_SRC[0], 0);
        cornerTop.x += cursorRect.width - 4;
        mCursorBitmap.blt(cornerTop.x, cornerTop.y, mWindowskin, CURSOR_CORNER_SRC[1], 0);
        final Rectangle cornerBottom = new Rectangle(cursorRect);
        cornerBottom.y += cursorRect.height - 4;
        mCursorBitmap.blt(cornerBottom.x, cornerBottom.y, mWindowskin, CURSOR_CORNER_SRC[2], 0);
        cornerBottom.x += cursorRect.width - 4;
        mCursorBitmap.blt(cornerBottom.x, cornerBottom.y, mWindowskin, CURSOR_CORNER_SRC[3], 0);

        // Side
        final Rectangle sideTB = new Rectangle(cursorRect);
        sideTB.y += 4;
        sideTB.width = 4;
        sideTB.height = cursorRect.height - 8;
        mCursorBitmap.stretchBlt(sideTB, mWindowskin, CURSOR_BORDER_SRC[0], 0);
        sideTB.x += cursorRect.width - 4;
        mCursorBitmap.stretchBlt(sideTB, mWindowskin, CURSOR_BORDER_SRC[1], 0);
        final Rectangle sideLR = new Rectangle(cursorRect);
        sideLR.width = cursorRect.width - 8;
        sideLR.height = 4;
        sideLR.x += 4;
        mCursorBitmap.stretchBlt(sideLR, mWindowskin, CURSOR_BORDER_SRC[2], 0);
        sideLR.y += cursorRect.height - 4;
        mCursorBitmap.stretchBlt(sideLR, mWindowskin, CURSOR_BORDER_SRC[3], 0);

        // Background
        final Rectangle backgroundRect = new Rectangle(cursorRect);
        backgroundRect.x += 4;
        backgroundRect.y += 4;
        backgroundRect.width -= 8;
        backgroundRect.height -= 8;
        mCursorBitmap.stretchBlt(backgroundRect, mWindowskin, CURSOR_BG_SRC, 0);

        mDirty = true;
    }

    public static IBitmap getDefaultWindowskin(final IApplication application) {
        try {
            return application.getGraphics().loadBitmap(DEFAULT_WINDOWSKIN);
        } catch (NoSuchFileException e) {
            return application.getGraphics().createBitmap(128, 128);
        }
    }
}
