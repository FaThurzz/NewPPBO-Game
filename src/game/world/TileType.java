package game.world;

import java.awt.Color;

/**
 * ENUM: TileType
 * Konsep OOP: Enum dengan field dan method
 */
public enum TileType {
    GRASS   (true,  new Color(80,  160, 80)),
    DIRT    (true,  new Color(139, 100, 60)),
    WATER   (false, new Color(60,  120, 200)),
    STONE   (false, new Color(120, 120, 120)),
    FARMLAND(true,  new Color(100, 70,  40)),
    PATH    (true,  new Color(180, 160, 100));

    private final boolean passable;
    private final Color   color;

    TileType(boolean passable, Color color) {
        this.passable = passable;
        this.color    = color;
    }

    public boolean isPassable() { return passable; }
    public Color   getColor()   { return color; }
}
