package game.engine;

import java.awt.Graphics2D;

/**
 * INTERFACE: Renderable
 * Konsep OOP: Interface
 * Setiap object yang bisa digambar ke layar wajib implement ini.
 */
public interface Renderable {
    void render(Graphics2D g, int camX, int camY);
}
