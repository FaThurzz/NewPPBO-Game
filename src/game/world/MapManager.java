package game.world;

import game.entity.Player;
import game.engine.GamePanel;
import game.exception.InvalidMapException;

/**
 * MAP MANAGER — mengatur pergantian antar map
 * Konsep OOP: Encapsulation
 *
 * Tugasnya:
 * - simpan map overworld dan cave
 * - tentukan map yang sedang aktif
 * - pindahkan player antar map dengan spawn point yang tepat
 */
public class MapManager {
    private TileMap currentMap;
    private TileMap overworldMap;
    private TileMap caveMap;

    public enum MapType {
        OVERWORLD, CAVE
    }

    private MapType currentMapType = MapType.OVERWORLD;

    public MapManager() {
        try {
            this.overworldMap = new TileMap();
            this.caveMap = new TileMap(true);

            this.overworldMap.setMapType(MapType.OVERWORLD);
            this.caveMap.setMapType(MapType.CAVE);

            this.currentMap = overworldMap;
            this.currentMapType = MapType.OVERWORLD;
        } catch (InvalidMapException e) {
            System.err.println("Error inisialisasi map: " + e.getMessage());
            this.overworldMap = new TileMap(true);
            this.caveMap = new TileMap(true);

            this.overworldMap.setMapType(MapType.OVERWORLD);
            this.caveMap.setMapType(MapType.CAVE);

            this.currentMap = overworldMap;
            this.currentMapType = MapType.OVERWORLD;
        }
    }


    /**
     * Pindah ke cave
     * Set posisi player ke spawn point cave (di entrance)
     */
    public void enterCave(Player player) {
        currentMap = caveMap;
        currentMapType = MapType.CAVE;
        currentMap.setMapType(currentMapType);
        player.setTileMap(caveMap);

        // Cari posisi entrance di cave
        TilePos entrancePos = caveMap.findEntrance();
        if (entrancePos != null) {
            player.setX(entrancePos.getCol() * GamePanel.TILE_SCALED);
            player.setY(entrancePos.getRow() * GamePanel.TILE_SCALED);
        } else {
            // Fallback jika entrance tidak ditemukan
            player.setX(3 * GamePanel.TILE_SCALED);
            player.setY(5 * GamePanel.TILE_SCALED);
        }

        System.out.println("Masuk ke cave!");
    }

    /**
     * Kembali ke overworld
     * Set posisi player ke spawn point overworld (di entrance)
     */
    public void exitCave(Player player) {
        currentMap = overworldMap;
        currentMapType = MapType.OVERWORLD;
        currentMap.setMapType(currentMapType);
        player.setTileMap(overworldMap);

        // Cari posisi entrance di overworld
        TilePos entrancePos = overworldMap.findEntrance();
        if (entrancePos != null) {
            player.setX(entrancePos.getCol() * GamePanel.TILE_SCALED);
            player.setY(entrancePos.getRow() * GamePanel.TILE_SCALED);
        } else {
            // Fallback jika entrance tidak ditemukan
            player.setX(15 * GamePanel.TILE_SCALED);
            player.setY(5 * GamePanel.TILE_SCALED);
        }

        System.out.println("Keluar dari cave!");
    }


    // ── Getters ─────────────────────────────────────────────
    public TileMap getCurrentMap()     { return currentMap; }
    public MapType getCurrentMapType() { return currentMapType; }
    public TileMap getOverworldMap()   { return overworldMap; }
    public TileMap getCaveMap()        { return caveMap; }
}
