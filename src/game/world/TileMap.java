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

    private static final int[][] DEFAULT_MAP = {
        {0,0,0,0,0,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,2,2,2,2,0,0,0,1,1,1,1,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,1,4,4,1,0,0,6,6,0,0,0},
        {0,0,0,4,4,4,0,0,0,1,4,4,1,0,0,0,0,0,0,0},
        {0,0,0,4,4,4,0,0,0,1,1,1,1,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
        {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
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
        TileType.PASSABLESTONE, TileType.UNBREAKSTONE
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


    /** Render semua tile yang terlihat di layar */
    public void render(Graphics2D g, Camera cam) {
        int ts = GamePanel.TILE_SCALED;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int sx = c * ts - cam.x;
                int sy = r * ts - cam.y;
                if (sx + ts < 0 || sx > GamePanel.SCREEN_WIDTH)  continue;
                if (sy + ts < 0 || sy > GamePanel.SCREEN_HEIGHT) continue;
                tiles[r][c].render(g, sx, sy, ts);

                // Tampilkan indikator visual FarmTile jika sudah dicangkul
                FarmTile farm = farmData[r][c];
                if (farm.isTilled()) {
                    renderFarmOverlay(g, farm, sx, sy, ts);
                }
            }
        }
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
            int stage = farm.getGrowStage();
            g.setColor(stage >= 3 ? new Color(255, 200, 50) : new Color(80, 180, 80));
            int ph = 8 + stage * 6; // makin tinggi sesuai stage
            g.fillRect(x + size/2 - 3, y + size - ph - 4, 6, ph);
        }
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
}
