package game.items;

import game.entity.Player;
import game.world.FarmTile;
import game.world.TileMap;
import game.world.TilePos;
import game.engine.ImageLoader;

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
        this.icon = ImageLoader.load("resources/items/tools/"
                + name.toLowerCase().replace(" ", "_") + ".png");
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

    private static String tierOf(int level) {
        if (level >= 25) return "Legendary";
        if (level >= 20) return "Epic";
        if (level >= 15) return "Rare";
        if (level >= 10) return "Uncommon";
        if (level >= 5)  return "Common";
        if (level >= 0)  return "Basic";
        return "Unknown";
    }

    public String getTierName() {
        return tierOf(this.level);
    }

    @Override
    public String getTooltip() {
        return super.getTooltip() + "\nTier: " + getTierName();
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

                    // Buat objek Crop berdasarkan nama tanaman yang tersimpan
                    // di FarmTile. Switch expression mengembalikan objek Crop
                    // yang sesuai, atau Crop generik jika tidak dikenal.
                    Crop hasil = switch (farm.getCropType()) {
                        case "Stroberi" -> new Crop("Stroberi", 150, 20, true);
                        case "Carrot"   -> new Crop("Carrot",   100, 15, true);
                        case "Potato"   -> new Crop("Potato",   120, 18, true);
                        default         -> new Crop(farm.getCropType(), 50, 10, true);
                    };

                    // Coba masukkan hasil panen ke inventory player.
                    // addItem() mengembalikan false jika inventory penuh.
                    boolean masuk = player.getInventory().addItem(hasil);

                    if (masuk) {
                        System.out.println("Panen " + farm.getCropType() + "!");
                        farm.reset(); // bersihkan tile setelah panen
                        player.setStamina(player.getStamina() - energyCost);
                    } else {
                        System.out.println("Inventory penuh, tidak bisa panen!");
                    }
                } else {
                    System.out.println("Belum waktunya panen.");
                }
            }
            case AXE -> {
                if (tileMap.isLog(target)) {
                    if(tileMap.cutLog(target)) {
                        player.setStamina(player.getStamina() - energyCost);
                        System.out.println("Pohon ditebang!");
                    }
                } else {
                    System.out.println("Tidak ada pohon untuk ditebang.");
                }
            }
            case PICKAXE -> {
                if (tileMap.isStone(target)) {
                    if (tileMap.breakStone(target)) {
                        player.setStamina(player.getStamina() - energyCost);
                        System.out.println("Batu hancur!");
                    }
                } else {
                    System.out.println("Tidak ada batu untuk dihancurkan.");
                }
            }
            case SWORD -> {
                player.setStamina(player.getStamina() - energyCost);
                System.out.println("Menyerang!");
            }
        }
    }

    // ── Factory methods ───────────────────────────────────
    public static Tool Hoe()         { return new Tool("Hoe",        ToolType.HOE,          2, 0); }
    public static Tool WateringCan() { return new Tool("Watering Can",     ToolType.WATERING_CAN, 2, 0); }
    public static Tool Scythe()      { return new Tool("Scythe",           ToolType.SCYTHE,       1, 0); }
    public static Tool Axe()         { return new Tool("Axe",        ToolType.AXE,          4, 0); }
    public static Tool Pickaxe()     { return new Tool("Pickaxe",    ToolType.PICKAXE,      4, 0); }
    public static Tool Sword()       { return new Tool("Sword",      ToolType.SWORD,        4, 0); }

    // Getter
    public ToolType getToolType()  { return toolType; }
    public int      getEnergyCost(){ return energyCost; }
    public int      getLevel()     { return level; }
}
