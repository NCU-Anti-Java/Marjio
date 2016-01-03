package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.graphics.IFont;

/**
 * @author Jason
 */
public class Font implements IFont {
    private String mName;
    private int mSize;
    private boolean mItalic;
    private boolean mBold;

    public Font(String name, int size, boolean italic, boolean bold) {
        mName = name;
        mSize = size;
        mItalic = italic;
        mBold = bold;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int getSize() {
        return mSize;
    }

    @Override
    public boolean isItalic() {
        return mItalic;
    }

    @Override
    public boolean isBold() {
        return mBold;
    }
}
