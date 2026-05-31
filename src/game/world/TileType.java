package game.world;

import java.awt.Color;

/**
 * ENUM: TileType
 * Konsep OOP: Enum dengan field dan method
 */
public enum TileType {
    GRASS   (true,  new Color(80,  160, 80)),
    DIRT    (true,  new Color(139, 100, 60)),
    LOG     (false, new Color(128, 50, 38)),
    ENTRANCE(true, new Color(120, 80, 160)),
    WATER   (false, new Color(60,  120, 200)),
    STONE   (false, new Color(120, 120, 120)),
    FARMLAND(true,  new Color(100, 70,  40)),
    PATH    (true,  new Color(180, 160, 100)),
    UNBREAKSTONE(false, new Color(52, 52, 52)),
    PASSABLESTONE(true, new Color(83, 82, 82)),
    SHOP(false, new Color(180, 120, 60)),
    HOUSE(false, new Color(207, 65, 133));

    private final boolean passable;
    private final Color   color;

    TileType(boolean passable, Color color) {
        this.passable = passable;
        this.color    = color;
    }

    public boolean isPassable() { return passable; }
    public Color   getColor()   { return color; }
}
