package io.github.antijava.marjio.graphics;

import io.github.antijava.marjio.common.IGraphics;
import io.github.antijava.marjio.common.exception.ObjectDisposedException;
import io.github.antijava.marjio.common.graphics.Color;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.IFont;
import io.github.antijava.marjio.common.graphics.Rectangle;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by Jason on 2015/12/26.
 */
public class Bitmap implements IBitmap {
    private final IGraphics mGraphics;
    private IFont mFont;

    private Graphics2D mAwtGraphics2D;
    BufferedImage mImage;
    private Font mAwtTextFont;
    private final FontRenderContext mAwtFontRenderContext;

    public Bitmap(IGraphics graphics, int width, int height) {
        mGraphics = graphics;
        mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        mAwtGraphics2D = mImage.createGraphics();
        mAwtGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Font properties
        setFont(mGraphics.getDefaultFont());
        mAwtFontRenderContext = new FontRenderContext(null, true, true);
    }

    public Bitmap(IGraphics graphics, BufferedImage image) {
        mGraphics = graphics;
        mImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        mAwtGraphics2D = mImage.createGraphics();
        mAwtGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        mAwtGraphics2D.drawImage(image, 0, 0, null);

        // Font properties
        setFont(mGraphics.getDefaultFont());
        mAwtFontRenderContext = new FontRenderContext(null, true, true);
    }

    // region Drawing
    @Override
    public void clear() {
        if (isDisposed())
            throw new ObjectDisposedException();
        clearRect(0, 0, mImage.getWidth(), mImage.getHeight());
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        if (isDisposed())
            throw new ObjectDisposedException();
        mAwtGraphics2D.clearRect(x, y, width, height);
    }

    @Override
    public void clearRect(Rectangle rect) {
        if (isDisposed())
            throw new ObjectDisposedException();
        clearRect(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, Color color, TextAlign align) {
        if (isDisposed())
            throw new ObjectDisposedException();

        // Calculate clipping bounds
        Rectangle bounds = measureText(text, lineHeight);
        bounds.width = maxWidth;

        // Apply clipping bounds
        mAwtGraphics2D.clipRect(x, y, bounds.width, bounds.height);

        // Draw
        final TextLayout layout = new TextLayout(text.toString(), mAwtTextFont, mAwtFontRenderContext);
        mAwtGraphics2D.setColor(convertToAwtColor(color));
        layout.draw(mAwtGraphics2D, x, y);

        // Remove clipping bounds
        mAwtGraphics2D.clipRect(0, 0, mImage.getWidth(), mImage.getHeight());
    }

    @Override
    public void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, TextAlign align) {
        drawText(text, x, y, maxWidth, lineHeight, Color.BLACK, align);
    }

    @Override
    public void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight, Color color) {
        drawText(text, x, y, maxWidth, lineHeight, color, TextAlign.LEFT);
    }

    @Override
    public void drawText(CharSequence text, int x, int y, int maxWidth, int lineHeight) {
        drawText(text, x, y, maxWidth, lineHeight, Color.BLACK, TextAlign.LEFT);
    }

    @Override
    public Rectangle measureText(CharSequence text, int lineHeight) {
        if (isDisposed())
            throw new ObjectDisposedException();

        final TextLayout layout = new TextLayout(text.toString(), mAwtTextFont, mAwtFontRenderContext);
        final Rectangle2D bounds = layout.getBounds();
        return new Rectangle((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight());
    }

    @Override
    public void fillAll(Color color) {
        fillRect(0, 0, mImage.getWidth(), mImage.getHeight(), color);
    }

    @Override
    public void fillRect(int x, int y, int width, int height, Color color) {
        if (isDisposed())
            throw new ObjectDisposedException();

        mAwtGraphics2D.setColor(convertToAwtColor(color));
        mAwtGraphics2D.fillRect(x, y, width, height);
    }

    @Override
    public void fillRect(Rectangle rect, Color color) {
        fillRect(rect.x, rect.y, rect.width, rect.height, color);
    }

    @Override
    public Color getPixel(int x, int y) {
        if (isDisposed())
            throw new ObjectDisposedException();

        return new Color(mImage.getRGB(x, y));
    }

    @Override
    public void setPixel(int x, int y, Color color) {
        if (isDisposed())
            throw new ObjectDisposedException();

        mImage.setRGB(x, y, color.toIntBits());
    }

    @Override
    public void blt(int x, int y, IBitmap src, Rectangle srcRect, int opacity) {
        if (isDisposed())
            throw new ObjectDisposedException();
        if (!(src instanceof Bitmap))
            throw new RuntimeException("Source bitmap should be " + Bitmap.class);

        final Bitmap srcBitmap = (Bitmap) src;
        final BufferedImage srcSubimage = srcBitmap.mImage.getSubimage(srcRect.x, srcRect.y, srcRect.width, srcRect.height);
        final Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - ((float) opacity / 255.0f));
        final Composite oldComposite = mAwtGraphics2D.getComposite();
        mAwtGraphics2D.setComposite(composite);
        mAwtGraphics2D.drawImage(srcSubimage, x, y, null);
        mAwtGraphics2D.setComposite(oldComposite);
    }

    @Override
    public void stretchBlt(int x, int y, int width, int height, IBitmap src, Rectangle srcRect, int opacity) {
        if (isDisposed())
            throw new ObjectDisposedException();
        if (!(src instanceof Bitmap))
            throw new RuntimeException("Source bitmap should be " + Bitmap.class);

        final Bitmap srcBitmap = (Bitmap) src;
        final BufferedImage srcSubimage = srcBitmap.mImage.getSubimage(srcRect.x, srcRect.y, srcRect.width, srcRect.height);
        final Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - ((float) opacity / 255.0f));
        final Composite oldComposite = mAwtGraphics2D.getComposite();
        mAwtGraphics2D.setComposite(composite);
        mAwtGraphics2D.drawImage(srcSubimage, x, y, width, height, null);
        mAwtGraphics2D.setComposite(oldComposite);
    }

    @Override
    public void stretchBlt(Rectangle rect, IBitmap src, Rectangle srcRect, int opacity) {
        stretchBlt(rect.x, rect.y, rect.width, rect.height, src, srcRect, opacity);
    }

    @Override
    public void tileBlt(int x, int y, int width, int height, IBitmap src, Rectangle srcRect, int opacity) {
        if (isDisposed())
            throw new ObjectDisposedException();
        if (!(src instanceof Bitmap))
            throw new RuntimeException("Source bitmap should be " + Bitmap.class);

        final Bitmap srcBitmap = (Bitmap) src;
        final BufferedImage srcSubimage = srcBitmap.mImage.getSubimage(srcRect.x, srcRect.y, srcRect.width, srcRect.height);
        final TexturePaint texturePaint = new TexturePaint(srcSubimage, new java.awt.Rectangle(srcRect.width, srcRect.height));
        final Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - ((float) opacity / 255.0f));
        final Paint oldPaint = mAwtGraphics2D.getPaint();
        final Composite oldComposite = mAwtGraphics2D.getComposite();
        mAwtGraphics2D.setPaint(texturePaint);
        mAwtGraphics2D.setComposite(composite);
        mAwtGraphics2D.translate(x, y);
        mAwtGraphics2D.fillRect(0, 0, width, height);
        mAwtGraphics2D.translate(-x, -y);
        mAwtGraphics2D.setPaint(oldPaint);
        mAwtGraphics2D.setComposite(oldComposite);
    }

    @Override
    public void tileBlt(Rectangle rect, IBitmap src, Rectangle srcRect, int opacity) {
        tileBlt(rect.x, rect.y, rect.width, rect.height, src, srcRect, opacity);
    }

    @Override
    public void resize(int width, int height) {
        if (isDisposed())
            throw new ObjectDisposedException();

        final Image scaledImage = mImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        mImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        mAwtGraphics2D = mImage.createGraphics();
        mAwtGraphics2D.drawImage(scaledImage, 0, 0, null);
    }
    // endregion Drawing

    // region Setter
    @Override
    public void setFont(IFont font) {
        mFont = font;

        int fontStyle = Font.PLAIN;
        if (font.isBold())
            fontStyle |= Font.BOLD;

        if (font.isItalic())
            fontStyle |= Font.ITALIC;

        mAwtTextFont = new Font(font.getName(), fontStyle, font.getSize());
    }
    // endregion Setter

    // region Getter
    @Override
    public int getHeight() {
        return mImage.getHeight();
    }

    @Override
    public int getWidth() {
        return mImage.getWidth();
    }

    @Override
    public IFont getFont() {
        return mFont;
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(mImage.getWidth(), mImage.getHeight());
    }
    // endregion Getter

    @Override
    public void dispose() {
        mAwtGraphics2D.dispose();
        mAwtGraphics2D = null;
        mImage = null;
        mAwtTextFont = null;
    }

    @Override
    public boolean isDisposed() {
        return mImage == null;
    }

    // region Helper
    private java.awt.Color convertToAwtColor(Color color) {
        return new java.awt.Color(color.toIntBits());
    }
    // endregion Helper
}
