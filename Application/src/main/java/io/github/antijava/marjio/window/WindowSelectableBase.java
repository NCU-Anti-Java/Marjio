package io.github.antijava.marjio.window;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.input.Key;
import io.github.antijava.marjio.input.Input;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Davy on 2015/12/28.
 */
public class WindowSelectableBase extends WindowBase {
    static final int SPACING = 32;
    private int mItemMax = 1, mColumnMax = 1, mIndex = -1;
    private int mSpacing;

    public WindowSelectableBase(@NotNull IApplication application, @NotNull IBitmap windowskin,
                                final int width, final int height, final int spacing) {
        super(application, windowskin, width, height);
        mSpacing = spacing;
    }

    public WindowSelectableBase(@NotNull IApplication application, final int width, final int height, final int spacing) {
        super(application, width, height);
        mSpacing = spacing;
    }

    public WindowSelectableBase(@NotNull IApplication application, @NotNull IBitmap windowskin,
                                final int width, final int height) {
        this(application, windowskin, width, height, SPACING);
    }

    public WindowSelectableBase(@NotNull IApplication application, final int width, final int height) {
        this(application, width, height, SPACING);
    }

    // region Getter
    public int getIndex() {
        return mIndex;
    }

    public int getItemMax() {
        return mItemMax;
    }

    public int getColumnMax() {
        return mColumnMax;
    }

    public int getRowMax() {
        return (mItemMax + mColumnMax - 1) / mColumnMax;
    }

    public int getTopRow() {
        return 0;
    }

    public int getPageRowMax() {
        return (getHeight() - 32) / WINDOW_LINE_HEIGHT;
    }

    public int getPageItemMax() {
        return getPageRowMax() * mColumnMax;
    }

    public int getBottomRow() {
        return getTopRow() + getPageRowMax() - 1;
    }
    // endregion Getter

    // region Setter
    public void setIndex(final int index) {
        mIndex = index;
        updateCursor();
    }

    public void setTopRow(final int row) {
        // TODO
    }

    public void setBottomRow(final int row) {
        setTopRow(row - (getPageRowMax() - 1));
    }
    // endregion Setter

    // region Overrides
    @Override
    public void update() {
        super.update();

        if (isCursorMovable()) {
            final IInput input = getApplication().getInput();
            if (input.isPressing(Key.DOWN))
                moveCursorDown(input.isPressed(Key.DOWN));
            // TODO: other dirs
        }
        updateCursor();
    }
    // endregion Overrides

    // region Helper
    private void updateCursor() {
        if (mIndex < 0) { // No cursor
            setCursorRect(null);
            return;
        }

        // TODO: Scrollable?
        // final int row = mIndex / mColumnMax;
        final Rectangle rect = getItemRect(mIndex);
        setCursorRect(rect);
    }

    private Rectangle getItemRect(final int index) {
        final Rectangle rect = new Rectangle();
        // TODO: content size
        rect.width = (getWidth() - 16 + mSpacing) / mColumnMax - mSpacing;
        rect.height = WINDOW_LINE_HEIGHT;
        rect.x = index % mColumnMax * (rect.width + mSpacing);
        rect.y = index / mColumnMax * WINDOW_LINE_HEIGHT;
        return rect;
    }

    private boolean isCursorMovable() {
        // TODO: if (isActive())
        if (mIndex < 0 || mIndex > mItemMax || mItemMax == 0)
            return false;
        return true;
    }
    // endregion Helper

    // region Move Cursor
    private void moveCursorDown(final boolean wrap) {
        if (mIndex < mItemMax - mColumnMax || (wrap && mColumnMax == 1))
            mIndex = (mIndex + mColumnMax) % mItemMax;
    }
    // endregion Move Cursor
}
