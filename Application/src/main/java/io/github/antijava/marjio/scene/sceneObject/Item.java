package io.github.antijava.marjio.scene.sceneObject;

/**
 * @author Jason
 */
public class Item {
    private final String name;
    private final ItemType type;

    // region Enum
    public enum ItemType {
        None,
        Trap
    }
    // endregion Enum

    // region Constructor
    public Item() {
        this(ItemType.None);
    }

    public Item(ItemType type) {
        this.type = type;

        switch (type) {
            case Trap:
                name = "Trap";
                break;
            default:
                name = "";
        }
    }
    // endregion Constructor

    // region Getter
    public String getName() {
        return name;
    }
    public ItemType getType() {
        return type;
    }
    // endregion Getter
}
