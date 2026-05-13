package game.items;

import game.entity.Player;
import game.world.FarmTile;
import game.world.TileMap;
import game.world.TilePos;

/**
 * TOOL — alat seperti cangkul, kaleng air, sabit, dll
 * Konsep OOP: Inheritance, Polymorphism, nested Enum (ToolType)
 */
public class Tool extends Item {

    public enum ToolType { HOE, WATERING_CAN, AXE, PICKAXE, SCYTHE, SWORD }

    private final ToolType toolType;
    private final int      energyCost; // stamina yang dikurangi per pemakaian
    private final int      level;      // 1=Basic, 2=Copper, dst

    public Tool(String name, ToolType toolType, int energyCost, int level) {
        super(name, descOf(toolType), ItemType.TOOL, 1, 0, 0);
        this.toolType   = toolType;
        this.energyCost = energyCost;
        this.level      = level;
    }

    private static String descOf(ToolType t) {
        return switch (t) {
            case HOE          -> "Mencangkul tanah menjadi lahan pertanian.";
            case WATERING_CAN -> "Menyiram tanaman agar tumbuh.";
            case AXE          -> "Menebang pohon dan semak.";
            case PICKAXE      -> "Memecah batu dan ore.";
            case SCYTHE       -> "Memanen tanaman yang sudah matang.";
            case SWORD        -> "Senjata untuk bertarung.";
        };
    }

    @Override public boolean isUsable() { return true; }

    @Override
    public void use(Player player, TileMap tileMap) {
        if (player.getStamina() < energyCost) {
            System.out.println("Stamina tidak cukup!");
            return;
        }

        TilePos  target = player.getFacingTile();
        FarmTile farm   = tileMap.getFarmTile(target);

        switch (toolType) {
            case HOE -> {
                if (farm != null && !farm.isTilled() && tileMap.isFarmland(target)) {
                    farm.till();
                    player.setStamina(player.getStamina() - energyCost);
                    System.out.println("Tanah dicangkul!");
                } else System.out.println("Tidak bisa mencangkul di sini.");
            }
            case WATERING_CAN -> {
                if (farm != null && farm.isTilled() && farm.isHasPlant()) {
                    farm.water();
                    player.setStamina(player.getStamina() - energyCost);
                    System.out.println("Tanaman disiram!");
                } else System.out.println("Tidak ada tanaman untuk disiram.");
            }
            case SCYTHE -> {
                if (farm != null && farm.isHarvestable()) {
                    System.out.println("Panen " + farm.getCropType() + "!");
                    farm.reset();
                    player.setStamina(player.getStamina() - energyCost);
                } else System.out.println("Belum waktunya panen.");
            }
            case AXE, PICKAXE -> {
                player.setStamina(player.getStamina() - energyCost);
                System.out.println(name + " digunakan.");
            }
            case SWORD -> {
                player.setStamina(player.getStamina() - energyCost);
                System.out.println("Menyerang!");
            }
        }
    }

    // ── Factory methods ───────────────────────────────────
    public static Tool basicHoe()         { return new Tool("Basic Hoe",        ToolType.HOE,          2, 1); }
    public static Tool basicWateringCan() { return new Tool("Watering Can",     ToolType.WATERING_CAN, 2, 1); }
    public static Tool basicScythe()      { return new Tool("Scythe",           ToolType.SCYTHE,       1, 1); }
    public static Tool basicAxe()         { return new Tool("Basic Axe",        ToolType.AXE,          4, 1); }
    public static Tool basicPickaxe()     { return new Tool("Basic Pickaxe",    ToolType.PICKAXE,      4, 1); }
    public static Tool basicSword()       { return new Tool("Basic Sword",      ToolType.SWORD,        4, 1); }

    // Getter
    public ToolType getToolType()  { return toolType; }
    public int      getEnergyCost(){ return energyCost; }
    public int      getLevel()     { return level; }
}
