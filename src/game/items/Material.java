package game.items;

import game.entity.Player;
import game.world.TileMap;

/**
 * MATERIAL — bahan baku untuk crafting, tidak bisa langsung dipakai
 * Konsep OOP: Inheritance, Polymorphism, Enum dalam class (nested enum)
 */
public class Material extends Item {

    public enum Grade { COMMON, UNCOMMON, RARE }

    private final Grade grade;

    public Material(String name, String description, int sellPrice, Grade grade) {
        super(name, description, ItemType.MATERIAL, 999, sellPrice, 0);
        this.grade = grade;
    }

    @Override public boolean isUsable() { return false; }

    @Override
    public void use(Player player, TileMap tileMap) {
        System.out.println(name + " adalah bahan baku. Gunakan untuk crafting atau dijual.");
    }

    public Grade getGrade() { return grade; }
}
