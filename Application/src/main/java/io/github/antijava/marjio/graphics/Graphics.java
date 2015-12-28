package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IFont;
import io.github.antijava.marjio.common.graphics.ISprite;
import io.github.antijava.marjio.common.graphics.Viewport;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

/**
 * Created by Jason on 2015/12/28.
 */
public class Graphics implements IGraphics {
    private ArrayList<Font> mFonts;
    private ArrayList<Viewport> mViewports;

    public Graphics() {
        mFonts = new ArrayList<>();
        mFonts.add(new Font("MingLiu", 12, false, false));

        mViewports = new ArrayList<>();
        mViewports.add(new Viewport(0, 0));
    }

    @Override
    public void update() {
        // TODO: Finish implement
        ArrayList<ISprite> sprites = SpriteBase.getSprites();

        for (ISprite sprite : sprites) {
            IBitmap bitmap = sprite.getBitmap();
            Viewport viewport = sprite.getViewport();
        }
    }

    @Override
    public IFont getDefaultFont() {
        return mFonts.get(0);
    }

    @Override
    public Viewport getDefaultViewport() {
        return mViewports.get(0);
    }

    @Override
    public IBitmap createBitmap(int width, int height) {
        return new Bitmap(this, width, height);
    }

    @Override
    public IBitmap loadBitmap(String path) throws NoSuchFileException {
        return null;
    }
}
