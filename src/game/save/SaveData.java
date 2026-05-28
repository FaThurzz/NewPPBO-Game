package game.save;

public class SaveData {
    // Player stats
    public int     playerX, playerY;
    public int     hp, maxHp;
    public int     stamina, maxStamina;
    public int     level, exp, money;

    // Waktu
    public int     day;
    public String  season;

    // Map
    public String  currentMap     = "OVERWORLD";
    public String  overworldTiles = "";
    public String  overworldFarm  = "";
    public String  caveTiles      = "";
    public String  caveFarm       = "";

    // Inventory
    // Format tiap item: "nama|qty|tipe|subtype"
    // Contoh: "Stroberi Seeds|5|SEED|Stroberi" atau "Hoe|1|TOOL|HOE"
    public String  hotbarData    = "";
    public String  backpackData  = "";

    public boolean valid = false;
}
