package io.github.antijava.marjio.graphics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Davy on 2015/12/29.
 */
public class Viewport extends io.github.antijava.marjio.common.graphics.Viewport
        implements Comparable {
    private final List<Sprite> mSpriteList = new ArrayList<>();

    // region Sprite List
    void addSprite(Sprite sprite) {
        if (sprite.getViewport() == this)
            return;
        mSpriteList.add(sprite);
    }

    void removeSprite(Sprite sprite) {
        if (sprite.getViewport() != this)
            return;
        mSpriteList.remove(sprite);
    }

    List<Sprite> getSprites() {
        return mSpriteList;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (!(o instanceof Viewport))
            return 0;

        final Viewport v = (Viewport) o;
        if (v.z == this.z)
            return v.y < this.y ? 1 : -1;
        return v.z < this.z ? 1 : -1;
    }
    // endregion Sprite List
}
