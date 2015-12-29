package io.github.antijava.marjio.window;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IBitmap.TextAlign;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.input.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Davy on 2015/12/29.
 */
public class WindowIPAddressInput extends WindowBase {
    private int mIndex = 0; // includes '.'
    private final int mIP[] = new int[] { 0, 0, 0, 0 };

    public WindowIPAddressInput(@NotNull final IApplication application) {
        super(application, 280, WINDOW_LINE_HEIGHT + 32);
        refresh();
    }

    @Override
    public void update() {
        super.update();

        if (isActive()) {
            final IInput input = getApplication().getInput();
            if (input.isRepeat(Key.LEFT)) {
                mIndex -= 1;
                if (mIndex % 4 == 3)
                    mIndex -= 1;
                if (mIndex < 0)
                    mIndex = 0;
            }
            if (input.isRepeat(Key.RIGHT)) {
                mIndex += 1;
                if (mIndex % 4 == 3)
                    mIndex += 1;
                if (mIndex > 14)
                    mIndex = 14;
            }
            if (input.isRepeat(Key.UP)) {
                final int part = mIndex / 4;
                final int _10base = (int) Math.pow(10, 2 - mIndex % 4);
                final int before = (mIP[part] / _10base / 10) * _10base * 10;
                final int after = mIP[part] % _10base;
                int val = (mIP[part] / _10base);
                do {
                    val = (val + 9) % 10;
                    mIP[part] = before + val * _10base + after;
                } while (0 > mIP[part] || mIP[part] > 255);
                refresh();
            }
            if (input.isRepeat(Key.DOWN)) {
                final int part = mIndex / 4;
                final int _10base = (int) Math.pow(10, 2 - mIndex % 4);
                final int before = (mIP[part] / _10base / 10) * _10base * 10;
                final int after = mIP[part] % _10base;
                int val = (mIP[part] / _10base);
                do {
                    val = (val + 1) % 10;
                    mIP[part] = before + val * _10base + after;
                } while (0 > mIP[part] || mIP[part] > 255);
                refresh();
            }
        }
        updateCursor();
    }

    public String getIPString() {
        return String.format("%3d.%3d.%3d.%3d", mIP[0], mIP[1], mIP[2], mIP[3]);
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(final int index) {
        mIndex = index;
        if (mIndex < 0)
            mIndex = 0;
        if (mIndex % 4 == 3)
            mIndex += 1;
        if (mIndex > 14)
            mIndex = 14;

        updateCursor();
    }

    private void updateCursor() {
        setCursorRect(new Rectangle(mIndex * 16, 0, 16, WINDOW_LINE_HEIGHT));
    }

    private void refresh() {
        final IBitmap content = getContent();
        content.clear();

        // Drawing ip string
        final String ipStr = getIPString();
        for (int i = 0; i < ipStr.length(); ++i)
            content.drawText(ipStr.substring(i, i + 1), i * 16, 0, 16, WINDOW_LINE_HEIGHT,
                    Color.WHITE, TextAlign.CENTER);

        dirty();
    }
}
