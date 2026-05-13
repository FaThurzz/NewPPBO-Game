package game.world;

import game.engine.GamePanel;
import game.entity.Entity;

/**
 * CAMERA — mengikuti player dan membatasi agar tidak keluar map
 * Konsep OOP: Encapsulation
 */
public class Camera {
    public int x, y;

    /** Update posisi kamera agar selalu memusatkan target di layar */
    public void follow(Entity target, TileMap map) {
        int ts = GamePanel.TILE_SCALED;

        x = target.getX() - GamePanel.SCREEN_WIDTH  / 2 + ts / 2;
        y = target.getY() - GamePanel.SCREEN_HEIGHT / 2 + ts / 2;

        // Clamp: jangan sampai kamera keluar batas map
        int mapW = map.getCols() * ts;
        int mapH = map.getRows() * ts;
        x = Math.max(0, Math.min(x, mapW - GamePanel.SCREEN_WIDTH));
        y = Math.max(0, Math.min(y, mapH - GamePanel.SCREEN_HEIGHT));
    }
}
