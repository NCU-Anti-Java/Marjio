package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.constant.SceneObjectConstant;
import io.github.antijava.marjio.graphics.Bitmap;
import io.github.antijava.marjio.scene.SceneBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Created by Zheng-Yuan on 12/29/2015.
 */
public class SceneMap extends SceneBase implements SceneObjectConstant {
    private final static String MAPFILE = "map/map";
    private int mRow;
    private int mCol;
    private Block[][] mMap;

    public SceneMap(IApplication application, int level) {
        super(application);

        if (level == 1)
            loadMapFile(MAPFILE);
        else
            getApplication().getLogger().log(Level.INFO, "Attemp to load map level > 1");
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

    private void loadMapFile(String filepath) {
        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final File file = new File(classLoader.getResource(filepath).toURI());
            final Scanner scanner = new Scanner(file);
            mRow = scanner.nextInt();
            mCol = scanner.nextInt();
            mMap = new Block[mRow][mCol];
            for (int i = 0; i < mRow; i++) {
                for (int j = 0; j < mCol; j++) {
                    final int type = scanner.nextInt();
                    final Viewport viewport = getApplication().getGraphics().getDefaultViewport();
                    mMap[i][j] = new Block(type, i * BLOCK_SIZE, j * BLOCK_SIZE, viewport);
                }
            }
        } catch (NullPointerException ex) {
            getApplication().getLogger().log(Level.INFO, filepath + "can not be found.");
        } catch (Exception ex) {
            getApplication().getLogger().log(Level.INFO, ex.toString());
        }
    }
}
