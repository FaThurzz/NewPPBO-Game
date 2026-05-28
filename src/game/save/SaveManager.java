package game.save;

import game.entity.Player;
import game.items.*;
import game.world.*;

import java.io.*;
import java.util.Properties;

public class SaveManager {

    private static final String SAVE_FILE = "savegame.properties";

    // ── SAVE ─────────────────────────────────────────────────
    public static void save(Player player, TimeSystem time, MapManager mapManager) {
        Properties props = new Properties();

        // ── Player stats ──────────────────────────────────
        props.setProperty("player.x",          String.valueOf(player.getX()));
        props.setProperty("player.y",          String.valueOf(player.getY()));
        props.setProperty("player.hp",         String.valueOf(player.getHp()));
        props.setProperty("player.maxHp",      String.valueOf(player.getMaxHp()));
        props.setProperty("player.stamina",    String.valueOf(player.getStamina()));
        props.setProperty("player.maxStamina", String.valueOf(player.getMaxStamina()));
        props.setProperty("player.level",      String.valueOf(player.getLevel()));
        props.setProperty("player.exp",        String.valueOf(player.getExp()));
        props.setProperty("player.money",      String.valueOf(player.getMoney()));

        // ── Waktu ─────────────────────────────────────────
        props.setProperty("time.day",    String.valueOf(time.getDay()));
        props.setProperty("time.season", time.getSeason().name());

        // ── Map aktif ─────────────────────────────────────
        props.setProperty("current.map", mapManager.getCurrentMapType().name());

        // ── State overworld ───────────────────────────────
        TileMap overworld = mapManager.getOverworldMap();
        props.setProperty("overworld.tiles", overworld.serializeTiles());
        props.setProperty("overworld.farm",  serializeFarm(overworld));

        // ── State cave ────────────────────────────────────
        TileMap cave = mapManager.getCaveMap();
        props.setProperty("cave.tiles", cave.serializeTiles());
        props.setProperty("cave.farm",  serializeFarm(cave));

        // ── Inventory ─────────────────────────────────────
        Inventory inv = player.getInventory();
        props.setProperty("inventory.hotbar",   serializeInventory(inv, false));
        props.setProperty("inventory.backpack", serializeInventory(inv, true));

        try (OutputStream out = new FileOutputStream(SAVE_FILE)) {
            props.store(out, "Meadow Tales Save Data");
            System.out.println("Game tersimpan!");
        } catch (IOException e) {
            System.err.println("Gagal menyimpan: " + e.getMessage());
        }
    }

    // ── LOAD ─────────────────────────────────────────────────
    public static SaveData load() {
        SaveData data = new SaveData();
        File file = new File(SAVE_FILE);
        if (!file.exists()) return data;

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);

            // Player stats
            data.playerX    = Integer.parseInt(props.getProperty("player.x",    "240"));
            data.playerY    = Integer.parseInt(props.getProperty("player.y",    "240"));
            data.hp         = Integer.parseInt(props.getProperty("player.hp",   "100"));
            data.maxHp      = Integer.parseInt(props.getProperty("player.maxHp","100"));
            data.stamina    = Integer.parseInt(props.getProperty("player.stamina",    "100"));
            data.maxStamina = Integer.parseInt(props.getProperty("player.maxStamina", "100"));
            data.level      = Integer.parseInt(props.getProperty("player.level", "1"));
            data.exp        = Integer.parseInt(props.getProperty("player.exp",   "0"));
            data.money      = Integer.parseInt(props.getProperty("player.money", "500"));

            // Waktu
            data.day    = Integer.parseInt(props.getProperty("time.day", "1"));
            data.season = props.getProperty("time.season", "SPRING");

            // Map
            data.currentMap     = props.getProperty("current.map",      "OVERWORLD");
            data.overworldTiles = props.getProperty("overworld.tiles",  "");
            data.overworldFarm  = props.getProperty("overworld.farm",   "");
            data.caveTiles      = props.getProperty("cave.tiles",       "");
            data.caveFarm       = props.getProperty("cave.farm",        "");

            // Inventory
            data.hotbarData   = props.getProperty("inventory.hotbar",   "");
            data.backpackData = props.getProperty("inventory.backpack", "");

            data.valid = true;
            System.out.println("Game dimuat!");

        } catch (IOException | NumberFormatException e) {
            System.err.println("Gagal memuat save: " + e.getMessage());
        }
        return data;
    }

    // ── Helper: serialisasi inventory ────────────────────────
    /**
     * Format tiap item: "nama|qty|tipe|subtype"
     * Antar item dipisah ";"
     *
     * Contoh hotbar:
     * "Hoe|1|TOOL|HOE;Watering Can|1|TOOL|WATERING_CAN;Stroberi|3|CROP|;"
     */
    private static String serializeInventory(Inventory inv, boolean isBackpack) {
        StringBuilder sb  = new StringBuilder();
        int size = isBackpack ? Inventory.BACKPACK_SIZE : Inventory.HOTBAR_SIZE;

        for (int i = 0; i < size; i++) {
            Item item = isBackpack ? inv.getBackpackItem(i) : inv.getItem(i);
            if (item == null) {
                sb.append("null");
            } else {
                // Tentukan subtype berdasarkan jenis item
                String subtype = "";
                if (item instanceof Tool t)   subtype = t.getToolType().name();
                if (item instanceof Seed s)   subtype = s.getCropType();
                if (item instanceof Crop)     subtype = "";
                if (item instanceof Food)     subtype = "";
                if (item instanceof Material m) subtype = m.getGrade().name();

                sb.append(item.getName()).append("|")
                        .append(item.getQuantity()).append("|")
                        .append(item.getType().name()).append("|")
                        .append(subtype);
            }
            if (i < size - 1) sb.append(";");
        }
        return sb.toString();
    }

    // ── Helper: serialisasi farm ──────────────────────────────
    private static String serializeFarm(TileMap map) {
        StringBuilder sb   = new StringBuilder();
        FarmTile[][]  farms = map.getAllFarmData();
        int rows = map.getRows();
        int cols = map.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                FarmTile f = farms[r][c];
                sb.append(f.isTilled()   ? "1" : "0").append(",");
                sb.append(f.isWatered()  ? "1" : "0").append(",");
                sb.append(f.isHasPlant() ? "1" : "0").append(",");
                sb.append(f.getCropType() != null ? f.getCropType() : "").append(",");
                sb.append(f.getGrowStage());
                if (!(r == rows - 1 && c == cols - 1)) sb.append(";");
            }
        }
        return sb.toString();
    }

    // ── Helper: rebuild item dari string ─────────────────────
    /**
     * Rekonstruksi objek Item dari string yang disimpan.
     * Format: "nama|qty|tipe|subtype"
     */
    public static Item deserializeItem(String s) {
        if (s == null || s.equals("null") || s.isEmpty()) return null;

        String[] parts = s.split("\\|", -1);
        if (parts.length < 4) return null;

        String   name    = parts[0];
        int      qty     = Integer.parseInt(parts[1]);
        String   type    = parts[2];
        String   subtype = parts[3];

        Item item = switch (type) {
            case "TOOL" -> {
                Tool.ToolType tt = Tool.ToolType.valueOf(subtype);
                yield switch (tt) {
                    case HOE          -> Tool.Hoe();
                    case WATERING_CAN -> Tool.WateringCan();
                    case SCYTHE       -> Tool.Scythe();
                    case AXE          -> Tool.Axe();
                    case PICKAXE      -> Tool.Pickaxe();
                    case SWORD        -> Tool.Sword();
                };
            }
            case "SEED" -> {
                // Cocokkan dengan factory method yang ada
                yield switch (name) {
                    case "Stroberi Seeds" -> Seed.stroberi();
                    case "Carrot Seeds"   -> Seed.carrot();
                    case "Potato Seeds"   -> Seed.potato();
                    // Tambahkan benih lain di sini
                    default -> null;
                };
            }
            case "CROP"     -> new Crop(name, 50, 10, true);
            case "FOOD"     -> null; // tambahkan jika ada Food factory
            case "MATERIAL" -> {
                Material.Grade grade = Material.Grade.valueOf(
                        subtype.isEmpty() ? "COMMON" : subtype
                );
                yield new Material(name, "", 5, grade);
            }
            default -> null;
        };

        // Set quantity sesuai yang tersimpan
        if (item != null) item.setQuantity(qty);
        return item;
    }

    public static boolean hasSaveFile() { return new File(SAVE_FILE).exists(); }

    public static void deleteSave() {
        File f = new File(SAVE_FILE);
        if (f.exists()) f.delete();
    }
}