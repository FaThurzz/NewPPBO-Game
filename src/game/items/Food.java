package game.items;

import game.entity.Player;
import game.world.TileMap;
import game.engine.ImageLoader;

/**
 * FOOD — makanan yang restore HP dan stamina, bisa punya buff
 * Konsep OOP: Inheritance, Polymorphism
 */
public class Food extends Item {

    private final int    hpRestore;
    private final int    staminaRestore;
    private final String buffType;     // "speed" | "luck" | null
    private final int    buffDuration;

    public Food(String name, String description,
                int hpRestore, int staminaRestore,
                String buffType, int buffDuration,
                int sellPrice, int buyPrice) {
        super(name, description, ItemType.FOOD, 99, sellPrice, buyPrice);
        this.icon = ImageLoader.load("resources/items/foods/"
                + name.toLowerCase().replace(" ", "_") + ".png");
        this.hpRestore      = hpRestore;
        this.staminaRestore = staminaRestore;
        this.buffType       = buffType;
        this.buffDuration   = buffDuration;
    }

    @Override public boolean isUsable() { return true; }

    @Override
    public void use(Player player, TileMap tileMap) {
        player.setHp(player.getHp() + hpRestore);
        player.setStamina(player.getStamina() + staminaRestore);
        if (buffType != null) {
            System.out.println("Buff " + buffType + " aktif selama " + buffDuration + " menit!");
        }
        quantity--;
        System.out.println("Makan " + name + " | HP +" + hpRestore + " | Stamina +" + staminaRestore);
    }
    public int    getHpRestore()      { return hpRestore; }
    public int    getStaminaRestore() { return staminaRestore; }
    public String getBuffType()       { return buffType; }
    public int    getBuffDuration()   { return buffDuration; }
}
