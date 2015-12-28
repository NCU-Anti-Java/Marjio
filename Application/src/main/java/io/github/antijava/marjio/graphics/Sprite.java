package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.graphics.*;

/**
 * Created by Davy on 2015/12/29.
 */
public abstract class Sprite implements ISprite {
    private Viewport mViewport = null;

    @Override
    public void setViewport(io.github.antijava.marjio.common.graphics.Viewport viewport) {
        if (!(viewport instanceof Viewport))
            throw new RuntimeException("Viewport should be " + Viewport.class);

        final Viewport vp = (Viewport) viewport;
        vp.addSprite(this);

        if (mViewport != null)
            mViewport.removeSprite(this);

        mViewport = vp;
    }

    @Override
    public Viewport getViewport() {
        return mViewport;
    }
}
