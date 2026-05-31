package game.world;

import game.engine.GamePanel;
import game.exception.InvalidMapException;
import game.engine.ImageLoader;
import java.awt.image.BufferedImage;


import java.awt.*;

/**
 * TILE MAP — menyimpan seluruh layout peta dan data FarmTile
 * Konsep OOP: Encapsulation, Exception Handling
 * Merge: ditambahkan farmData (FarmTile[][]) dari versi temanmu
 * agar sistem tanam-siram-panen bisa berjalan.
 */
public class TileMap {

    private Tile[][]     tiles;
    private FarmTile[][] farmData;
    private int rows, cols;
    private MapManager.MapType currentMapType;
    private static final BufferedImage GRASS_IMG     = ImageLoader.load("resources/items/worlds/overworld/grass.png");
    private static final BufferedImage STONE_OVW_IMG     = ImageLoader.load("resources/items/worlds/overworld/stone_ovw.png");
    private static final BufferedImage STONE_CAVE_IMG     = ImageLoader.load("resources/items/worlds/cave/stone_cave.png");
    private static final BufferedImage WATER_IMG     = ImageLoader.load("resources/items/worlds/overworld/water.png");
    private static final BufferedImage DIRT_IMG      = ImageLoader.load("resources/items/worlds/overworld/dirt.png");
    private static final BufferedImage FARMLAND_IMG  = ImageLoader.load("resources/items/worlds/overworld/farmland.png");
    private static final BufferedImage PATH_IMG      = ImageLoader.load("resources/items/worlds/overworld/path.png");
    private static final BufferedImage LOG_IMG       = ImageLoader.load("resources/items/worlds/overworld/log.png");
    private static final BufferedImage ENTRANCE_IMG  = ImageLoader.load("resources/items/worlds/overworld/entrance.png");
    private static final BufferedImage STONEUNBREAK_IMG = ImageLoader.load("resources/items/worlds/cave/stone_unbreak.png");
    private static final BufferedImage PASSSTONE_A_IMG =
            ImageLoader.load("resources/items/worlds/cave/stone_passable_a.png");
    private static final BufferedImage PASSSTONE_B_IMG =
            ImageLoader.load("resources/items/worlds/cave/stone_passable_b.png");
    private static final BufferedImage SHOP_IMG =
            ImageLoader.load("resources/items/worlds/overworld/shop.png");
    private static final BufferedImage HOUSE_IMG =
            ImageLoader.load("resources/items/worlds/overworld/house.png");
    private static final BufferedImage CROP_STROBERI_IMG = ImageLoader.load("resources/items/seeds/Strawberry_Tanam.png");
    private static final BufferedImage CROP_POTATO_IMG   = ImageLoader.load("resources/items/seeds/Potato_Tanam.png");
    private static final BufferedImage CROP_WORTEL_IMG   = ImageLoader.load("resources/items/seeds/Wortel_Tanam.png");

    private static final int[][] DEFAULT_MAP = {
            {0,0,6,0,0,0,0,0,0,7,0,0,6,0,0,6,0,6,0,0}, // baris 0  — cave
            {0,0,0,2,2,2,0,0,0,5,0,0,0,6,0,6,0,6,0,0}, // baris 1  — jalan + pohon kanan
            {0,0,0,2,2,2,0,0,0,5,0,0,0,6,0,0,0,0,6,0}, // baris 2  — kolam + jalan + pohon
            {0,0,0,0,0,0,0,0,0,5,0,0,0,0,6,0,6,0,0,0}, // baris 3  — kolam + farm + jalan + pohon
            {0,0,4,4,4,4,4,0,0,5,0,0,0,0,0,6,0,0,6,0}, // baris 4  — farm + jalan + pohon
            {0,0,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5,0}, // baris 5  — farm + jalan horizontal tengah
            {0,0,4,4,4,4,4,0,0,5,0,0,0,0,0,0,0,0,0,0}, // baris 6  — farm + jalan
            {0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0}, // baris 7  — jalan
            {0,11,11,11,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0}, // baris 8  — rumah + jalan
            {0,11,11,11,0,0,0,0,0,5,5,5,5,5,5,5,5,10,10,0}, // baris 9  — rumah + jalan horizontal bawah + market
            {0,11,11,11,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0}, // baris 10 — rumah + jalan
            {0,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0}, // baris 11 — jalan
    };
    // 0=GRASS, 1=STONE, 2=WATER, 3=DIRT, 4=FARMLAND, 5=PATH
    private static final int[][] CAVE_MAP = {
        {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
        {9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,1,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,9},
        {9,8,8,8,8,8,8,8,8,7,8,8,8,8,8,8,8,8,8,9},
        {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
    };


    private static final TileType[] TYPE_MAP = {
        TileType.GRASS, TileType.STONE, TileType.WATER,
        TileType.DIRT,  TileType.FARMLAND, TileType.PATH, TileType.LOG, TileType.ENTRANCE,
        TileType.PASSABLESTONE, TileType.UNBREAKSTONE, TileType.SHOP, TileType.HOUSE
    };

    /** Constructor normal — bisa throw InvalidMapException */
    public TileMap() throws InvalidMapException {
        this.currentMapType = MapManager.MapType.OVERWORLD;
        loadMap(DEFAULT_MAP);
    }

    /** Constructor fallback — dipakai jika constructor normal gagal */
    public TileMap(boolean useFallback) {
        if (useFallback) {
            try {
                this.currentMapType = MapManager.MapType.CAVE;
                loadMap(CAVE_MAP);
            } catch (InvalidMapException e) {
                // fallback safe: set default empty grass
                rows = 12; cols = 20;
                tiles    = new Tile[rows][cols];
                farmData = new FarmTile[rows][cols];
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < cols; c++) {
                        tiles[r][c] = new Tile(TileType.GRASS, GRASS_IMG);

                        farmData[r][c] = new FarmTile();
                    }
            }
        } else {
            // sama seperti sekarang: default lawn
            rows = 12; cols = 20;
            tiles    = new Tile[rows][cols];
            farmData = new FarmTile[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) {
                    tiles[r][c] = new Tile(TileType.GRASS, GRASS_IMG);

                    farmData[r][c] = new FarmTile();
                }
        }
    }


    private void loadMap(int[][] mapData) throws InvalidMapException {
        if (mapData == null || mapData.length == 0)
            throw new InvalidMapException("Data map tidak boleh kosong!");

        rows = mapData.length;
        cols = mapData[0].length;

        for (int r = 0; r < rows; r++)
            if (mapData[r].length != cols)
                throw new InvalidMapException("Baris " + r + " tidak konsisten.");

        tiles    = new Tile[rows][cols];
        farmData = new FarmTile[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int id = mapData[r][c];
                if (id < 0 || id >= TYPE_MAP.length)
                    throw new InvalidMapException("ID tile tidak dikenal: " + id);
                TileType type = TYPE_MAP[id];
                tiles[r][c] = new Tile(type, textureOf(type, r, c));
                farmData[r][c] = new FarmTile();
            }
        }
    }

    public void setMapType(MapManager.MapType type) {
        this.currentMapType = type;
    }

    private BufferedImage textureOf(TileType type, int row, int col) {
        return switch (type) {
            case GRASS -> GRASS_IMG;
            case STONE -> currentMapType == MapManager.MapType.OVERWORLD ? STONE_OVW_IMG : STONE_CAVE_IMG;
            case WATER -> WATER_IMG;
            case DIRT -> DIRT_IMG;
            case FARMLAND -> FARMLAND_IMG;
            case PATH -> PATH_IMG;
            case LOG -> LOG_IMG;
            case ENTRANCE -> ENTRANCE_IMG;
            case UNBREAKSTONE -> STONEUNBREAK_IMG;
            case PASSABLESTONE -> ((row + col) & 1) == 0 ? PASSSTONE_A_IMG : PASSSTONE_B_IMG;
            case SHOP -> SHOP_IMG;
            case HOUSE -> HOUSE_IMG;
        };
    }

    /** Apakah tile di posisi ini bisa dilewati? */
    public boolean isPassable(int col, int row) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
        return tiles[row][col].isPassable();
    }

    /** Ambil FarmTile di posisi tertentu (untuk Tool dan Seed) */
    public FarmTile getFarmTile(TilePos pos) {
        int r = pos.getRow(), c = pos.getCol();
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return null;
        }
        return farmData[r][c];
    }

    /**
     * Cari posisi entrance di map
     * @return TilePos entrance, atau null jika tidak ada
     */
    public TilePos findEntrance() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tiles[r][c].getType() == TileType.ENTRANCE) {
                    return new TilePos(c, r);
                }
            }
        }
        return null; // Tidak ada entrance
    }


    public boolean isFarmland(TilePos pos) {
        if (pos == null) return false;

        int r = pos.getRow();
        int c = pos.getCol();

        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }

        return tiles[r][c].getType() == TileType.FARMLAND;
    }

    public boolean isStone(TilePos pos){
        if (pos == null) return false;

        int r = pos.getRow();
        int c = pos.getCol();

        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }

        return tiles[r][c].getType() == TileType.STONE;
    }

    public boolean isLog(TilePos pos){
        if (pos == null) return false;

        int r = pos.getRow();
        int c = pos.getCol();

        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }

        return tiles[r][c].getType() == TileType.LOG;
    }

    public boolean cutLog(TilePos pos){
        if(pos == null) return false;

        int r = pos.getRow();
        int c = pos.getCol();

        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        if (!isLog(pos)) return false;

        tiles[r][c] = new Tile(TileType.DIRT, DIRT_IMG);

        farmData[r][c].reset();

        return true;
    }

    public boolean breakStone(TilePos pos) {
        if (pos == null) return false;

        int r = pos.getRow();
        int c = pos.getCol();

        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        if (!isStone(pos)) return false;

        // Cek tipe map saat ini
        MapManager.MapType type = (currentMapType != null)
                ? currentMapType
                : MapManager.MapType.OVERWORLD;

        if (type == MapManager.MapType.OVERWORLD) {
            tiles[r][c] = new Tile(TileType.GRASS, GRASS_IMG);
        } else {
            tiles[r][c] = new Tile(TileType.PASSABLESTONE, textureOf(TileType.PASSABLESTONE, r, c));

        }

        // Reset farm data jika ada
        farmData[r][c].reset();

        return true;
    }

    public boolean isEntrance(TilePos pos) {
        if (pos == null) return false;
        int r = pos.getRow();
        int c = pos.getCol();
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        return tiles[r][c].getType() == TileType.ENTRANCE;
    }

    /**
     * Cek apakah posisi (playerX, playerY) dalam pixel
     * berada dalam jarak 1 tile dari tile SHOP manapun.
     * Cara kerja:
     * 1. Konversi posisi pixel player ke koordinat tile
     * 2. Cek tile-tile di sekitar player (radius 1)
     * 3. Jika ada tile SHOP di sekitarnya → return true
     */
    public boolean isNearShop(int playerX, int playerY) {
        int ts      = GamePanel.TILE_SCALED;
        int playerCol = playerX / ts;
        int playerRow = playerY / ts;

        // Cek radius 1 tile di sekitar player (termasuk diagonal)
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = playerRow + dr;
                int c = playerCol + dc;
                if (r < 0 || r >= rows || c < 0 || c >= cols) continue;
                if (tiles[r][c].getType() == TileType.SHOP) return true;
            }
        }
        return false;
    }

    /** Gambar overlay di atas tile pertanian (tilled, watered, tanaman) */
    private void renderFarmOverlay(Graphics2D g, FarmTile farm, int x, int y, int size) {
        // Overlay tanah yang sudah dicangkul
        g.setColor(new Color(80, 50, 20, 120));
        g.fillRect(x + 2, y + 2, size - 4, size - 4);

        // Overlay biru kalau sudah disiram
        if (farm.isWatered()) {
            g.setColor(new Color(60, 120, 200, 80));
            g.fillRect(x + 2, y + 2, size - 4, size - 4);
        }

        // Gambar tanaman sederhana jika ada
        if (farm.isHasPlant()) {
            String cropType = farm.getCropType();
            BufferedImage cropImg = switch (cropType != null ? cropType : "") {
                case "Stroberi" -> CROP_STROBERI_IMG;
                case "Potato"   -> CROP_POTATO_IMG;
                case "Carrot"   -> CROP_WORTEL_IMG;
                default         -> null;
            };
            if (cropImg != null) {
                g.drawImage(cropImg, x, y, size, size, null);
            }
        }
    }

    public void render(Graphics2D g, Camera cam) {
        int ts = GamePanel.TILE_SCALED;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int sx = c * ts - cam.x;
                int sy = r * ts - cam.y;
                if (sx + ts < 0 || sx > GamePanel.SCREEN_WIDTH)  continue;
                if (sy + ts < 0 || sy > GamePanel.SCREEN_HEIGHT) continue;

                // Skip HOUSE & SHOP, dirender terpisah
                if (tiles[r][c].getType() != TileType.HOUSE && tiles[r][c].getType() != TileType.SHOP) {
                    tiles[r][c].render(g, sx, sy, ts);
                }

                FarmTile farm = farmData[r][c];
                if (farm.isTilled()) {
                    renderFarmOverlay(g, farm, sx, sy, ts);
                }
            }
        }
        renderWaterAsOne(g, cam);
        renderShopAsOne(g, cam);
        renderHouseAsOne(g, cam);
    }

    private void renderWaterAsOne(Graphics2D g, Camera cam) {
        if (WATER_IMG == null) return;
        int ts = GamePanel.TILE_SCALED;
        int minR = Integer.MAX_VALUE, minC = Integer.MAX_VALUE;
        int maxR = -1, maxC = -1;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (tiles[r][c].getType() == TileType.WATER) {
                    if (r < minR) minR = r;
                    if (r > maxR) maxR = r;
                    if (c < minC) minC = c;
                    if (c > maxC) maxC = c;
                }

        if (maxR == -1) return;

        int x = minC * ts - cam.x;
        int y = minR * ts - cam.y;
        int w = (maxC - minC + 1) * ts;
        int h = (maxR - minR + 1) * ts;

        g.drawImage(WATER_IMG, x, y, w, h, null);
    }


    private void renderShopAsOne(Graphics2D g, Camera cam) {
        if (SHOP_IMG == null) return;
        int ts = GamePanel.TILE_SCALED;
        int minR = Integer.MAX_VALUE, minC = Integer.MAX_VALUE;
        int maxR = -1, maxC = -1;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (tiles[r][c].getType() == TileType.SHOP) {
                    if (r < minR) minR = r;
                    if (r > maxR) maxR = r;
                    if (c < minC) minC = c;
                    if (c > maxC) maxC = c;
                }

        if (maxR == -1) return;

        int x = minC * ts - cam.x;
        int y = minR * ts - cam.y;
        int w = (maxC - minC + 1) * ts;
        int h = (maxR - minR + 1) * ts;

        g.drawImage(SHOP_IMG, x, y, w, h, null);
    }

    private void renderHouseAsOne(Graphics2D g, Camera cam) {
        if (HOUSE_IMG == null) return;
        int ts = GamePanel.TILE_SCALED;
        int minR = Integer.MAX_VALUE, minC = Integer.MAX_VALUE;
        int maxR = -1, maxC = -1;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (tiles[r][c].getType() == TileType.HOUSE) {
                    if (r < minR) minR = r;
                    if (r > maxR) maxR = r;
                    if (c < minC) minC = c;
                    if (c > maxC) maxC = c;
                }

        if (maxR == -1) return;

        int x = minC * ts - cam.x;
        int y = minR * ts - cam.y;
        int w = (maxC - minC + 1) * ts;
        int h = (maxR - minR + 1) * ts;

        g.drawImage(HOUSE_IMG, x, y, w, h, null);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    /**
     * Dipanggil oleh TimeSystem.endDay() setiap pergantian hari.
     * Melakukan loop ke semua tile dan memanggil advanceDay()
     * pada setiap FarmTile — tanaman yang sudah disiram akan tumbuh.
     */
    public void advanceAllFarmTiles() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                farmData[r][c].advanceDay();
        // advanceDay() sudah ada di FarmTile:
        // → kalau hasPlant && watered: growStage++
        // → lalu watered direset ke false
    }
    // Getter semua farmData untuk SaveManager
    public FarmTile[][] getAllFarmData() {
        return farmData;
    }

    // Load farmData dari save
    public void loadFarmData(String savedFarm) {
        if (savedFarm == null || savedFarm.isEmpty()) return;

        String[] tiles = savedFarm.split(";");
        int idx = 0;

        for (int r = 0; r < rows && idx < tiles.length; r++) {
            for (int c = 0; c < cols && idx < tiles.length; c++) {
                String[] parts = tiles[idx++].split(",", -1);
                // -1 agar split tidak buang trailing empty string
                if (parts.length < 5) continue;

                FarmTile f = farmData[r][c];
                if ("1".equals(parts[0])) f.till();
                if ("1".equals(parts[1])) f.water();
                if ("1".equals(parts[2])) {
                    String crop = parts[3];
                    if (!crop.isEmpty()) f.plant(crop);
                }
                // Set growStage langsung lewat setter baru
                try {
                    f.setGrowStage(Integer.parseInt(parts[4]));
                } catch (NumberFormatException ignored) {}
            }
        }
    }
    /**
     * Serialisasi seluruh tipe tile menjadi string.
     * Format: angka ID tile dipisah koma, baris dipisah titik koma
     * Contoh: "0,0,1,2,0;0,4,4,0,0;..."
     */
    public String serializeTiles() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Cari ID dari tipe tile saat ini
                TileType type = tiles[r][c].getType();
                int id = tileTypeToId(type);
                sb.append(id);
                if (c < cols - 1) sb.append(",");
            }
            if (r < rows - 1) sb.append(";");
        }
        return sb.toString();
    }

    /**
     * Muat kembali tipe tile dari string yang disimpan.
     */
    public void deserializeTiles(String data) {
        if (data == null || data.isEmpty()) return;

        String[] rowData = data.split(";");
        for (int r = 0; r < rows && r < rowData.length; r++) {
            String[] colData = rowData[r].split(",");
            for (int c = 0; c < cols && c < colData.length; c++) {
                try {
                    int id = Integer.parseInt(colData[c].trim());
                    TileType type = idToTileType(id);
                    if (type != null) {
                        // Buat tile baru dengan tipe yang tersimpan
                        tiles[r][c] = new Tile(type, textureOf(type, r, c));
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }

// ── Helper: konversi TileType ↔ ID ───────────────────────

    private int tileTypeToId(TileType type) {
        return switch (type) {
            case GRASS         -> 0;
            case STONE         -> 1;
            case WATER         -> 2;
            case DIRT          -> 3;
            case FARMLAND      -> 4;
            case PATH          -> 5;
            case LOG           -> 6;
            case ENTRANCE      -> 7;
            case PASSABLESTONE -> 8;
            case UNBREAKSTONE  -> 9;
            case SHOP -> 10;
            case HOUSE -> 11;
        };
    }

    private TileType idToTileType(int id) {
        return switch (id) {
            case 0 -> TileType.GRASS;
            case 1 -> TileType.STONE;
            case 2 -> TileType.WATER;
            case 3 -> TileType.DIRT;
            case 4 -> TileType.FARMLAND;
            case 5 -> TileType.PATH;
            case 6 -> TileType.LOG;
            case 7 -> TileType.ENTRANCE;
            case 8 -> TileType.PASSABLESTONE;
            case 9 -> TileType.UNBREAKSTONE;
            case 10 -> TileType.SHOP;
            case 11 -> TileType.HOUSE;
            default -> null;
        };
    }
}
