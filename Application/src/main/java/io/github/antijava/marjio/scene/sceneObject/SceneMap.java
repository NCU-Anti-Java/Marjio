package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.application.Application;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.constant.SceneObjectConstant;
import io.github.antijava.marjio.graphics.Bitmap;
import io.github.antijava.marjio.resourcemanager.ResourcesManager;
import io.github.antijava.marjio.scene.SceneBase;
import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.Buffer;
import java.util.*;
import java.util.logging.Level;
/**
 * Created by Zheng-Yuan on 12/29/2015.
 */
public class SceneMap extends SceneBase implements SceneObjectConstant {
    private final static String MAPFILE = "map/map";
    private static final Map<Integer, Pair<Integer, Integer>> typeMap = new HashMap<>();
    static {
        typeMap.put(1, Pair.of(4, 1)); // Ground
        typeMap.put(2, Pair.of(8, 3)); // Wood
        typeMap.put(3, Pair.of(2, 5)); // Win Line
        typeMap.put(4, Pair.of(0, 0)); // Item block
        typeMap.put(5, Pair.of(0, 0)); // Item block
        typeMap.put(6, Pair.of(0, 0)); // Item block
        typeMap.put(7, Pair.of(0, 0)); // Item block
        typeMap.put(8, Pair.of(0, 0)); // Item block

    }
    private int mRow;
    private int mCol;
    private Block[][] mMap;

    public SceneMap(IApplication application, int level) {
        this(application,
                level,
                application.getGraphics().getDefaultViewport());
    }

    public SceneMap(IApplication application, int level, Viewport viewport) {
        super(application);

        if (level == 1)
            loadMapFile(MAPFILE, viewport);
        else
            getApplication()
                    .getLogger()
                    .log(Level.INFO, "Attemp to load map level > 1");
    }

    public int getRow() {
        return mRow;
    }

    public int getCol() {
        return mCol;
    }

    public List<Block> getAdjacentBlocks(Player player) {
        final int x = player.getX() / BLOCK_SIZE;
        final int y = player.getY() / BLOCK_SIZE;
        List<Block> result = new ArrayList<>();
        for (int ox = -1; ox <= 1; ox++) {
            for (int oy = -1; oy <= 1; oy++) {
                if (y + oy < 0 || y + oy >= mRow || x + ox < 0 || x + ox >= mCol) continue;
                result.add(mMap[y + oy][x + ox]);
            }
        }
        return result;
    }

    @Override
    public void update() {
        for (int i = 0; i < mRow; i++) {
            for (int j = 0; j < mCol; j++) {
                mMap[i][j].update();
            }
        }
    }

    public void setBlock(int row, int col, Block block) {
        mMap[row][col] = block;
    }

    public Block getBlock(int row, int col) {
        return mMap[row][col];
    }

    public boolean isInMap(int row, int col) {
        return (row >= 0 && row < mRow) &&
                (col >= 0 && col < mCol);
    }

    private void loadMapFile(String filepath, final Viewport viewport) {
        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final File file = new File(classLoader.getResource(filepath).getFile());
            final Scanner scanner = new Scanner(file);
            mRow = scanner.nextInt();
            mCol = scanner.nextInt();
            mMap = new Block[mRow][mCol];
            for (int i = 0; i < mRow; i++) {
                for (int j = 0; j < mCol; j++) {
                    final int type = scanner.nextInt();
                    final ResourcesManager rm = ((Application)getApplication())
                                                    .getResourcesManager();
                    final Pair<Integer, Integer> tileId = typeMap.get(type);
                    Bitmap tileBitmap = null;
                    if (tileId != null)
                        tileBitmap = rm.tile("default.png",
                                tileId.getLeft(), tileId.getRight());

                    mMap[i][j] = new Block(type,
                            j * BLOCK_SIZE, i * BLOCK_SIZE,
                            viewport, tileBitmap);
                }
            }
            scanner.close();
        } catch (FileNotFoundException ex) {
            getApplication().getLogger().log(Level.INFO, filepath + " can not be found.");
        } catch (Exception ex) {
            getApplication().getLogger().log(Level.INFO, ex.toString());
        }
    }

    private void loadImageMapFile(String filepath, final Viewport viewport) {
        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final BufferedImage image = ImageIO.read(classLoader.getResource(filepath));
            final ResourcesManager rm = ((Application)getApplication())
                                                    .getResourcesManager();
            mRow = image.getHeight();
            mCol = image.getWidth();

            mMap = new Block[mRow][mCol];

            for (int i = 0; i < mRow; i++) {
                for (int j = 0; j < mCol; j++) {
                    final int type = image.getRGB(j, i);
                    final Pair<Integer, Integer> tileId = typeMap.get(type);
                    Bitmap tileBitmap = null;

                    if (tileId != null)
                        tileBitmap = rm.tile("default.png",
                                tileId.getLeft(), tileId.getRight());

                    mMap[i][j] = new Block(type,
                            j * BLOCK_SIZE, i * BLOCK_SIZE,
                            viewport, tileBitmap);
                }
            }

        } catch (FileNotFoundException ex) {
            getApplication().getLogger().log(Level.INFO, filepath + " can not be found.");
        } catch (Exception ex) {
            getApplication().getLogger().log(Level.INFO, ex.toString());
        };
    }

    @Override
    public void dispose() {
        for (int i = 0; i < mRow; i++)
            for (int j = 0; j < mCol; j++)
                mMap[i][j].dispose();
    }

}
