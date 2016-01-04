package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.application.Application;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.IInput;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IFont;
import io.github.antijava.marjio.common.input.Event;
import io.github.antijava.marjio.constant.GameConstant;
import io.github.antijava.marjio.input.AWTKeyInfo;
import rx.Observable;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Davy
 * @author Jason
 */
public class Graphics implements IGraphics, GameConstant {
    private static final Viewport sDefaultViewport = new Viewport();
    private static final Font sDefaultFont = new Font("Microsoft JhengHei", 16, false, false);
    private final IApplication mApplication;
    private final ArrayList<Font> mFonts;
    private final ArrayList<Viewport> mViewports;
    private final JPanel mSwingPanel;
    private final BufferedImage mCanvas;
    private final Graphics2D mCanvasGraphics;
    private final Bitmap mFpsMeterBitmap;

    public Graphics(IApplication application) {
        mApplication = application;
        mCanvas = new BufferedImage(GAME_WIDTH, GAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        mCanvasGraphics = mCanvas.createGraphics();
        mCanvasGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        mCanvasGraphics.setBackground(Color.BLACK);
        mFpsMeterBitmap = (Bitmap) createBitmap(GAME_WIDTH, GAME_HEIGHT);

        mSwingPanel = new JPanel() {
            @Override
            public void paint(java.awt.Graphics g) {
                g.drawImage(mCanvas, 0, 0, null);
            }
        };

        mFonts = new ArrayList<>();
        mFonts.add(sDefaultFont);

        mViewports = new ArrayList<>();
        mViewports.add(sDefaultViewport);

        // Java Window
        final JFrame mFrame = new JFrame();
        mFrame.setSize(GAME_WIDTH, GAME_HEIGHT);
        mFrame.setResizable(false);
        mFrame.setLocationRelativeTo(null);
        mFrame.add(mSwingPanel);
        mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                final IInput input = mApplication.getInput();
                if (input == null)
                    return;

                input.triggerEvent(new Event(new AWTKeyInfo(e), Event.Type.Keyboard));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                final IInput input = mApplication.getInput();
                if (input == null)
                    return;

                input.triggerEvent(new Event(new AWTKeyInfo(e), Event.Type.Keyboard));
            }
        });
        new Thread(() -> {
            mFrame.setVisible(true); // Run window loop
        }).run();
    }

    @Override
    public void update() {
        // Clean up
        final java.awt.Graphics g = mCanvas.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        Observable.from(mViewports)
                .toSortedList()
                .flatMap(Observable::from)
                .toBlocking()
                .forEach(viewport -> {
                    Observable.from(viewport.getSprites())
                            .filter(sprite -> sprite.getBitmap() != null)
                            .toSortedList((sprite, sprite2) -> {
                                if (sprite.getZ() == sprite2.getZ())
                                    if (sprite.getY() == sprite.getY())
                                        return sprite.getX() > sprite2.getX() ? 1 : -1;
                                    else
                                        return sprite.getY() > sprite2.getY() ? 1 : -1;
                                else
                                    return sprite.getZ() > sprite2.getZ() ? 1 : -1;
                            })
                            .flatMap(Observable::from)
                            .toBlocking()
                            .forEach(sprite -> {
                                final Bitmap bitmap = (Bitmap) sprite.getBitmap();
                                mCanvasGraphics.drawImage(bitmap.mImage,
                                        sprite.getX() - viewport.ox + viewport.x,
                                        sprite.getY() - viewport.oy + viewport.y,
                                        (int) (bitmap.getWidth() * sprite.getZoomX()),
                                        (int) (bitmap.getHeight() * sprite.getZoomY()),
                                        null);
                            });
                });

        drawFps();

        mSwingPanel.repaint();
    }

    @Override
    public IFont getDefaultFont() {
        return sDefaultFont;
    }

    @Override
    public Viewport getDefaultViewport() {
        return sDefaultViewport;
    }

    public BufferedImage getBufferedImage() {
        return mCanvas;
    }

    @Override
    public IBitmap createBitmap(int width, int height) {
        return new Bitmap(this, width, height);
    }

    @Override
    public IBitmap loadBitmap(String path) throws NoSuchFileException {
        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final URL resource = classLoader.getResource(path);
            if (resource == null)
                throw new NoSuchFileException(path);
            final File file = new File(resource.getFile());
            final BufferedImage bufferedImage = ImageIO.read(file);
            return new Bitmap(this, bufferedImage);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Viewport createViewport() {
        final Viewport viewport = new Viewport();
        mViewports.add(viewport);
        return viewport;
    }

    @Override
    public IBitmap snapToBitmap() {
        return new Bitmap(this, mCanvas);
    }

    private void drawFps() {
        final Application application = (Application) mApplication;
        mFpsMeterBitmap.clear();

        mFpsMeterBitmap.drawText("Fps: " + application.getFps(), 1, 1, -1, 24,
                io.github.antijava.marjio.common.graphics.Color.BLACK);
        mFpsMeterBitmap.drawText("Fps: " + application.getFps(), 0, 0, -1, 24,
                io.github.antijava.marjio.common.graphics.Color.WHITE);

        mFpsMeterBitmap.drawText("Real Fps: " + application.getRealFps(), 1, 25, -1, 24,
                io.github.antijava.marjio.common.graphics.Color.BLACK);
        mFpsMeterBitmap.drawText("Real Fps: " + application.getRealFps(), 0, 24, -1, 24,
                io.github.antijava.marjio.common.graphics.Color.WHITE);

        mCanvasGraphics.drawImage(mFpsMeterBitmap.mImage, 0, 0, null);
    }
}
