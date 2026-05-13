package game.items;

import game.entity.Player;
import game.world.FarmTile;
import game.world.Season;
import game.world.TileMap;
import game.world.TilePos;

/**
 * SEED — benih yang bisa ditanam di FarmTile yang sudah dicangkul
 * Konsep OOP: Inheritance, Polymorphism
 */
public class Seed extends Item {

    private final String cropType;    // jenis tanaman yang tumbuh
    private final int    growthDays;  // hari yang dibutuhkan untuk panen
    private final Season validSeason; // musim valid untuk ditanam

    public Seed(String name, String cropType,
                int growthDays, Season validSeason,
                int sellPrice, int buyPrice) {
        super(name, "Benih " + cropType, ItemType.SEED, 99, sellPrice, buyPrice);
        this.cropType    = cropType;
        this.growthDays  = growthDays;
        this.validSeason = validSeason;
    }

    @Override public boolean isUsable() { return true; }

    @Override
    public void use(Player player, TileMap tileMap) {
        TilePos  target = player.getFacingTile();
        FarmTile farm   = tileMap.getFarmTile(target);

        if (farm == null)      { System.out.println("Tidak ada lahan di sini."); return; }
        if (!farm.isTilled())  { System.out.println("Tanah belum dicangkul!");   return; }
        if (farm.isHasPlant()) { System.out.println("Sudah ada tanaman.");        return; }

        farm.plant(cropType);
        quantity--;
        System.out.println("Berhasil menanam " + cropType + "!");
    }

    // ── Factory methods (cara mudah buat seed umum) ───────
    public static Seed parsnip() { return new Seed("Parsnip Seeds", "Parsnip", 4,  Season.SPRING, 10,  20); }
    public static Seed melon()   { return new Seed("Melon Seeds",   "Melon",   12, Season.SUMMER, 80,  200); }
    public static Seed pumpkin() { return new Seed("Pumpkin Seeds", "Pumpkin", 13, Season.FALL,   100, 200); }

    // Getter
    public String getCropType()    { return cropType; }
    public int    getGrowthDays()  { return growthDays; }
    public Season getValidSeason() { return validSeason; }
}
