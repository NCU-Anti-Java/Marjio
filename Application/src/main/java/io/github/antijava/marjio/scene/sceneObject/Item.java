package io.github.antijava.marjio.scene.sceneObject;

import io.github.antijava.marjio.application.Application;
import io.github.antijava.marjio.common.IApplication;
import io.github.antijava.marjio.common.graphics.IBitmap;
import io.github.antijava.marjio.common.graphics.Rectangle;
import io.github.antijava.marjio.common.graphics.Viewport;
import io.github.antijava.marjio.resourcemanager.ResourcesManager;

/**
 * @author Jason
 */
public class Item extends SceneObjectObjectBase {
    private final String mName;
    private final ItemType mItemType;

    // region Enum
    public enum ItemType {
        TrapTool,
        Trap,
        SpeedTool,
        Weapon,
        Bullet
    }
    // endregion Enum

    // region Constructor
    public Item(Viewport viewport, IApplication application, int x, int y, int z, ItemType type) {
        super(viewport);

        // Set properties
        setX(x);
        setY(y);
        setZ(z);
        this.mItemType = type;

        // Set bitmap for this item
        ResourcesManager rm = ((Application)application).getResourcesManager();
        IBitmap bitmap;
        switch (type) {
            case TrapTool:
                mName = "Trap Tool";
                bitmap = rm.tile("default.png", 18, 6);
                bitmap.resize(BLOCK_SIZE, BLOCK_SIZE);
                break;
            case Trap:
                mName = "Trap";
                bitmap = rm.tile("default.png", 15, 6);
                bitmap.resize(BLOCK_SIZE, BLOCK_SIZE);
                break;
            case SpeedTool:
                mName = "Speed Tool";
                bitmap = rm.tile("default.png", 11, 2);
                bitmap.resize(BLOCK_SIZE, BLOCK_SIZE);
                break;
            case Weapon:
                mName = "Weapon";
                bitmap = rm.tile("default.png", 27, 4);
                bitmap.resize(BLOCK_SIZE, BLOCK_SIZE);
                break;
            case Bullet:
                mName = "Bullet";
                bitmap = rm.tile("default.png", 27, 4);
                bitmap.resize(BLOCK_SIZE, BLOCK_SIZE);
                break;
            default:
                mName = "";
                bitmap = application.getGraphics().createBitmap(BLOCK_SIZE, BLOCK_SIZE);
        }
        setBitmap(bitmap);
    }
    // endregion Constructor

    // region Getter
    public String getName() {
        return mName;
    }

    public ItemType getItemType() {
        return mItemType;
    }

    @Override
    public Rectangle getOccupiedSpace() {
        return new Rectangle(getX(), getY(), BLOCK_SIZE, BLOCK_SIZE);
    }
    // endregion Getter
}
