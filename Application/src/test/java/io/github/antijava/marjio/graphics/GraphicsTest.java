package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IClient;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.ISceneManager;
import io.github.antijava.marjio.common.IServer;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IFont;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Jason
 */
public class GraphicsTest {
    Graphics mGraphics;

    @Before
    public void setUP() {
        // A blank IApplication instance
        final IApplication application = new IApplication() {
            @Override
            public void run() {}

            @Override
            public Logger getLogger() {
                return null;
            }

            @Override
            public ISceneManager getSceneManager() {
                return null;
            }

            @Override
            public IInput getInput() {
                return null;
            }

            @Override
            public IServer getServer() {
                return null;
            }

            @Override
            public IClient getClient() {
                return null;
            }

            @Override
            public IGraphics getGraphics() {
                return null;
            }
        };

        mGraphics = new Graphics(application);
    }

    /**
     * Try updating a canvas with an empty sprite list by 4 color of points.
     * All points should be Color.BLACK since it's a empty default canvas.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateEmpty() throws Exception {
        // Update
        mGraphics.update();

        // Test
        assertEquals(Color.BLACK, new Color(mGraphics.getBufferedImage().getRGB(0, 0)));
        assertEquals(Color.BLACK, new Color(mGraphics.getBufferedImage().getRGB(100, 50)));
        assertEquals(Color.BLACK, new Color(mGraphics.getBufferedImage().getRGB(50, 20)));
        assertEquals(Color.BLACK, new Color(mGraphics.getBufferedImage().getRGB(120, 320)));
    }

    /**
     * Try updating a canvas with a list contains a sprite, which is on (100, 100).
     *
     * @throws Exception
     */
    @Test
    public void testUpdateNotEmpty() throws Exception {
        // Add sprite
        final Viewport viewport = mGraphics.getDefaultViewport();
        final SpriteBase sprite = new SpriteBase(viewport);
        final IBitmap bitmap = mGraphics.loadBitmap("green.png");
        sprite.setBitmap(bitmap);
        sprite.setX(100);
        sprite.setY(100);

        // Update
        mGraphics.update();

        // Test on boundary
        assertEquals(new Color(0, 255, 0, 255), new Color(mGraphics.getBufferedImage().getRGB(100, 100)));
        assertEquals(new Color(0, 255, 0, 255), new Color(mGraphics.getBufferedImage().getRGB(299, 100)));
        assertEquals(new Color(0, 255, 0, 255), new Color(mGraphics.getBufferedImage().getRGB(100, 399)));
        assertEquals(new Color(0, 255, 0, 255), new Color(mGraphics.getBufferedImage().getRGB(299, 399)));

        // Test on outside of boundary
        assertEquals(Color.BLACK, new Color(mGraphics.getBufferedImage().getRGB(99, 99)));
        assertEquals(Color.BLACK, new Color(mGraphics.getBufferedImage().getRGB(300, 99)));
        assertEquals(Color.BLACK, new Color(mGraphics.getBufferedImage().getRGB(99, 400)));
        assertEquals(Color.BLACK, new Color(mGraphics.getBufferedImage().getRGB(300, 400)));
    }

    /**
     * Try getting default font and verify its properties.
     *
     * @throws Exception
     */
    @Test
    public void testGetDefaultFont() throws Exception {
        final IFont font = mGraphics.getDefaultFont();
        assertEquals("Microsoft JhengHei", font.getName());
        assertEquals(16, font.getSize());
        assertFalse(font.isItalic());
        assertFalse(font.isBold());
    }

    /**
     * Try getting default viewport and verify its properties.
     *
     * @throws Exception
     */
    @Test
    public void testGetDefaultViewport() throws Exception {
        final Viewport viewport = mGraphics.getDefaultViewport();
        assertTrue(viewport.getSprites().isEmpty());
    }

    /**
     * Try creating a bitmap and verify its properties.
     *
     * @throws Exception
     */
    @Test
    public void testCreateBitmap() throws Exception {
        final IBitmap bitmap = mGraphics.createBitmap(100, 200);
        assertEquals(100, bitmap.getWidth());
        assertEquals(200, bitmap.getHeight());
        assertEquals(new Color(0, 0, 0, 0), bitmap.getPixel(50, 50));
    }

    /**
     * Try loading a existing green bitmap and verify its properties.
     *
     * @throws Exception
     */
    @Test
    public void testLoadBitmapExist() throws Exception {
        final IBitmap bitmap = mGraphics.loadBitmap("green.png");
        assertNotNull(bitmap);
        assertEquals(300, bitmap.getHeight());
        assertEquals(200, bitmap.getWidth());
        assertEquals(new Color(0, 255, 0, 255), bitmap.getPixel(100, 100));
    }

    /**
     * Try creating a non-existing bitmap.
     *
     * @throws Exception
     */
    @Test
    public void testLoadBitmapNotExist() throws Exception {
        final IBitmap bitmap = mGraphics.loadBitmap("NotExist");
        assertNull(bitmap);
    }
}