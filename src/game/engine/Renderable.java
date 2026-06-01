package game.engine;

import java.awt.Graphics2D;

/**
 * INTERFACE: Renderable
 * Konsep OOP: Interface
 */
public interface Renderable {
    void render(Graphics2D g, int camX, int camY);
}
