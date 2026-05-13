package game.world;

import game.engine.GamePanel;
import game.exception.InvalidMapException;

import java.awt.*;

/**
 * TILE MAP — menyimpan seluruh layout peta dan data FarmTile
 * Konsep OOP: Encapsulation, Exception Handling
 *
 * Merge: ditambahkan farmData (FarmTile[][]) dari versi temanmu
 * agar sistem tanam-siram-panen bisa berjalan.
 */
public class TileMap {

    private Tile[][]     tiles;
    private FarmTile[][] farmData;
    private int rows, cols;

    private static final int[][] DEFAULT_MAP = {
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,2,2,2,2,0,0,0,1,1,1,1,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,1,4,4,1,0,0,0,0,0,0,0},
        {0,0,0,4,4,4,0,0,0,1,4,4,1,0,0,0,0,0,0,0},
        {0,0,0,4,4,4,0,0,0,1,1,1,1,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
        {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
    };
    // 0=GRASS, 1=STONE, 2=WATER, 3=DIRT, 4=FARMLAND, 5=PATH

    private static final TileType[] TYPE_MAP = {
        TileType.GRASS, TileType.STONE, TileType.WATER,
        TileType.DIRT,  TileType.FARMLAND, TileType.PATH
    };

    /** Constructor normal — bisa throw InvalidMapException */
    public TileMap() throws InvalidMapException {
        loadMap(DEFAULT_MAP);
    }

    /** Constructor fallback — dipakai jika constructor normal gagal */
    public TileMap(boolean useFallback) {
        rows = 12; cols = 20;
        tiles    = new Tile[rows][cols];
        farmData = new FarmTile[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                tiles[r][c]    = new Tile(TileType.GRASS, null);
                farmData[r][c] = new FarmTile();
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
                tiles[r][c]    = new Tile(TYPE_MAP[id], null);
                farmData[r][c] = new FarmTile();
            }
        }
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

    public boolean isFarmland(TilePos pos) {
        if (pos == null) return false;

        int r = pos.getRow();
        int c = pos.getCol();

        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }

        return tiles[r][c].getType() == TileType.FARMLAND;
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
}
