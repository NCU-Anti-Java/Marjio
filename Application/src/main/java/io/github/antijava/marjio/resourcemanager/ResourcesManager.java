package io.github.antijava.marjio.resourcemanager;

import io.github.antijava.marjio.application.Application;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.constant.ResourcesConstant;
import io.github.antijava.marjio.graphics.Bitmap;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Davy on 2015/12/29.
 */
public class ResourcesManager implements ResourcesConstant {
    private final Application mApplication;
    private final Bitmap mEmptyBitmap;
    private final Map<String, Bitmap> mCachedBitmaps;
    private final Map<Triple<String, Integer, Integer>, Bitmap> mCachedTiles;
    private final Map<Triple<String, Integer, Integer>, Bitmap> mCachedMarios;

    public ResourcesManager(@NotNull final Application application) {
        mApplication = application;
        mEmptyBitmap = (Bitmap) mApplication.getGraphics().createBitmap(1, 1);
        mCachedBitmaps = new HashMap<>();
        mCachedTiles = new HashMap<>();
        mCachedMarios = new HashMap<>();
    }

    public Bitmap emptyBitmap() {
        return mEmptyBitmap;
    }

    public Bitmap windowskin(final String path) {
        return readBitmap(RESOURCE_WINDOWSKIN_PATH + path);
    }

    public Bitmap tilemap(final String path) {
        return readBitmap(RESOURCE_TILEMAP_PATH + path);
    }

    public Bitmap tile(final String path, final int x, final int y) {
        final Triple<String, Integer, Integer> key = Triple.of(path, x, y);
        if (mCachedTiles.containsKey(key))
            return mCachedTiles.get(key);

        final Bitmap tilemap = tilemap(path);
        final Bitmap tile = (Bitmap) mApplication.getGraphics().createBitmap(16, 16);
        tile.blt(0, 0, tilemap, new Rectangle(x * 18, y * 18, 16, 16), 0);
        mCachedTiles.put(key, tile);
        return tile;
    }

    public Bitmap mariomap(final String path) {
        return readBitmap(RESOURCE_MARIO_PATH + path);
    }

    public Bitmap mario(final String path, final int x, final int y) {
        final Triple<String, Integer, Integer> key = Triple.of(path, x, y);
        if (mCachedMarios.containsKey(key))
            return mCachedMarios.get(key);

        final Bitmap mariomap = mariomap(path);
        final Bitmap mario = (Bitmap) mApplication.getGraphics().createBitmap(16, 16);
        mario.blt(0, 0, mariomap, new Rectangle(1 + x * 18, 1 + y * 18, 16, 16), 0);
        mCachedTiles.put(key, mario);
        return mario;
    }

    private Bitmap readBitmap(final String path) {
        if (mCachedBitmaps.containsKey(path))
            return mCachedBitmaps.get(path);

        try {
            final Bitmap bitmap = (Bitmap) mApplication.getGraphics().loadBitmap(path);
            mCachedBitmaps.put(path, bitmap);
            return bitmap;
        } catch (NoSuchFileException e) {
            return null;
        }
    }
}
