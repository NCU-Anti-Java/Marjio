package io.github.antijava.marjio.scene.sceneObject;

/**
 * @author Jason
 */
public class Item {
    private final String name;

    // region Enum
    enum ItemType {
        Trap
    }
    // endregion Enum

    // region Constructor
    public Item(ItemType type) {
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
    // endregion Getter
}
