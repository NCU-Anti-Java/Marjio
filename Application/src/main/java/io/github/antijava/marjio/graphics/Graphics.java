package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IFont;
import io.github.antijava.marjio.common.graphics.ISprite;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.constant.GameConstant;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

/**
 * @author Davy
 * @author Jason
 */
public class Graphics implements IGraphics, GameConstant {
    private static final Viewport sDefaultViewport = new Viewport();
    private static final Font sDefaultFont = new Font("MingLiu", 12, false, false);
    private final IApplication mApplication;
    private final ArrayList<Font> mFonts;
    private final ArrayList<Viewport> mViewports;
    private final JPanel mSwingPanel;
    private final BufferedImage mCanvas;
    private final Graphics2D mCanvasGraphics;

    public Graphics(IApplication application) {
        mApplication = application;
        mCanvas = new BufferedImage(GAME_WIDTH, GAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        mCanvasGraphics = mCanvas.createGraphics();
        mCanvasGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        mCanvasGraphics.setBackground(Color.BLACK);

        mSwingPanel = new JPanel() {
            @Override
            public void paint(java.awt.Graphics g) {
                g.drawImage(mCanvas, 0, 0, null);
            }
        };

        // Java Window
        final JFrame mFrame = new JFrame();
        mFrame.setSize(GAME_WIDTH, GAME_HEIGHT);
        mFrame.setResizable(false);
        mFrame.setLocationRelativeTo(null);
        mFrame.add(mSwingPanel);
        mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        new Thread(() -> {
            mFrame.setVisible(true); // Run window loop
        }).run();

        mFonts = new ArrayList<>();
        mFonts.add(sDefaultFont);

        mViewports = new ArrayList<>();
        mViewports.add(sDefaultViewport);
    }

    @Override
    public void update() {
        // Clean up
        final java.awt.Graphics g = mCanvas.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        // TODO: Finish implement
        ArrayList<ISprite> sprites = SpriteBase.getSprites();
        for (ISprite sprite : sprites) {
            IBitmap bitmap = sprite.getBitmap();
            Viewport viewport = sprite.getViewport();
        }

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

    @Override
    public IBitmap createBitmap(int width, int height) {
        return new Bitmap(this, width, height);
    }

    @Override
    public IBitmap loadBitmap(String path) throws NoSuchFileException {
        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final File file = new File(classLoader.getResource(path).getFile());
            final BufferedImage bufferedImage = ImageIO.read(file);
            return new Bitmap(this, bufferedImage);
        } catch (IOException e) {
            return null;
        }
    }
}
