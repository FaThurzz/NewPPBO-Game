package game.world;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * TILE — satu kotak di peta
 * Konsep OOP: Encapsulation
 */
public class Tile {

    private final TileType    type;
    private final BufferedImage image;
    private final boolean     passable;

    public Tile(TileType type, BufferedImage image) {
        this.type     = type;
        this.image    = image;
        this.passable = type.isPassable();
    }

    public void render(Graphics2D g, int x, int y, int size) {
        if (image != null) {
            g.drawImage(image, x, y, size, size, null);
        } else {
            g.setColor(type.getColor());
            g.fillRect(x, y, size, size);
            g.setColor(new Color(0, 0, 0, 30));
            g.drawRect(x, y, size, size);
        }
    }

    public boolean isPassable() { return passable; }
    public TileType getType()   { return type; }
}
