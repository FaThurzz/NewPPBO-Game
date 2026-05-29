package game.entity;

import game.engine.Renderable;
import game.engine.Updatable;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * ABSTRACT CLASS: Entity
 *
 * Konsep OOP:
 * 1. Abstract Class  — tidak bisa di-instansiasi langsung
 * 2. Inheritance     — Player mewarisi semua field dan method di sini
 * 3. Encapsulation   — field protected, diakses lewat getter/setter
 * 4. Interface       — implements Renderable dan Updatable
 */
public abstract class Entity implements Renderable, Updatable {

    // protected = bisa diakses subclass (Player), tapi tidak dari luar
    protected int x, y;
    protected int width, height;
    protected int speed;
    protected BufferedImage sprite;
    protected boolean active = true;

    protected Entity(int x, int y, int width, int height, int speed) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
        this.speed  = speed;
    }

    // Wajib diisi subclass (Polymorphism)
    @Override public abstract void update();
    @Override public abstract void render(Graphics2D g, int camX, int camY);

    /** Hitbox untuk collision detection */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /** Cek tabrakan dengan entity lain (Polymorphism: menerima tipe Entity) */
    public boolean collidesWith(Entity other) {
        return getBounds().intersects(other.getBounds());
    }

    // ── Getters & Setters (Encapsulation) ─────────────────
    public int getX()      { return x; }
    public int getY()      { return y; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }
    public int getSpeed()  { return speed; }

    public void setX(int x)      { this.x = x; }
    public void setY(int y)      { this.y = y; }
    public void setSpeed(int s)  { this.speed = s; }
}
