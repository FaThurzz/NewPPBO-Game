package game.items;

import game.entity.Player;
import game.world.TileMap;

import java.awt.image.BufferedImage;

/**
 * ABSTRACT CLASS: Item
 *
 * Konsep OOP:
 * 1. Abstract Class — cetakan dasar semua item
 * 2. Inheritance    — Crop, Food, Material, Seed, Tool extends Item
 * 3. Encapsulation  — field protected, getter tersedia
 * 4. Polymorphism   — use() dan isUsable() dipanggil secara generik
 */
public abstract class Item {

    protected String      name;
    protected String      description;
    protected int         quantity;
    protected ItemType    type;
    protected int         maxStack;
    protected int         sellPrice;
    protected int         buyPrice;
    protected BufferedImage icon;

    protected Item(String name, String description, ItemType type,
                   int maxStack, int sellPrice, int buyPrice) {
        this.name        = name;
        this.description = description;
        this.type        = type;
        this.maxStack    = maxStack;
        this.sellPrice   = sellPrice;
        this.buyPrice    = buyPrice;
        this.quantity    = 1;
    }

    /** Apakah item ini bisa dipakai? (Polymorphism) */
    public abstract boolean isUsable();

    /** Efek saat item dipakai (Polymorphism) */
    public abstract void use(Player player, TileMap tileMap);

    /** Cek apakah item ini bisa ditumpuk dengan item lain */
    public boolean canStackWith(Item other) {
        return other != null
            && other.name.equals(this.name)
            && this.quantity < this.maxStack;
    }

    /** Tooltip untuk ditampilkan saat hover */
    public String getTooltip() {
        return name + "\n" + description + "\nJual: $" + sellPrice
            + (buyPrice > 0 ? " | Beli: $" + buyPrice : "");
    }

    // ── Getters (Encapsulation) ────────────────────────────
    public String       getName()       { return name; }
    public String       getDescription(){ return description; }
    public ItemType     getType()       { return type; }
    public int          getQuantity()   { return quantity; }
    public int          getMaxStack()   { return maxStack; }
    public int          getSellPrice()  { return sellPrice; }
    public int          getBuyPrice()   { return buyPrice; }
    public BufferedImage getIcon()      { return icon; }

    public void setQuantity(int q) { this.quantity = Math.max(0, q); }
}
