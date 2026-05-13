package game.items;

import game.entity.Player;
import game.world.TileMap;

/**
 * CROP — hasil panen, bisa dimakan langsung jika edible=true
 * Konsep OOP: Inheritance (extends Item), Polymorphism (override use/isUsable)
 */
public class Crop extends Item {

    private final int     energy; // stamina dipulihkan jika dimakan
    private final boolean edible; // apakah bisa dimakan langsung?

    public Crop(String name, int sellPrice, int energy, boolean edible) {
        super(name, "Hasil panen segar.", ItemType.CROP, 99, sellPrice, 0);
        this.energy = energy;
        this.edible = edible;
    }

    @Override public boolean isUsable() { return edible; }

    @Override
    public void use(Player player, TileMap tileMap) {
        if (!edible) { System.out.println(name + " tidak bisa dimakan langsung."); return; }
        player.setStamina(player.getStamina() + energy);
        quantity--;
        System.out.println("Makan " + name + " | Stamina +" + energy);
    }
}
