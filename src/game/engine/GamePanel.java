package game.engine;

import game.entity.Player;
import game.exception.InvalidMapException;
import game.items.InventoryRenderer;
import game.items.Item;
import game.items.Tool;
import game.menu.MainMenu;
import game.menu.MenuRenderer;
import game.save.SaveData;
import game.save.SaveManager;
import game.store.Store;
import game.store.StoreRenderer;
import game.world.Camera;
import game.world.TileMap;
import game.world.TilePos;
import game.world.MapManager;
import game.world.TimeSystem;

import javax.swing.*;
import java.awt.*;

/**
 * GAME PANEL — koordinator utama
 * Konsep OOP: Encapsulation
 * Merge:
 * - Game loop pakai javax.swing.Timer
 * - Camera dari world.Camera
 * - InventoryRenderer hotbar
 * - HUD HP + Stamina + Level + Money
 */
public class GamePanel extends JPanel {

    // ── Konstanta layar ────────────────────────────────────
    public static final int TILE_SIZE = 16;
    public static final int SCALE = 3;
    public static final int TILE_SCALED = TILE_SIZE * SCALE; // 48px
    public static final int SCREEN_COLS = 16;
    public static final int SCREEN_ROWS = 12;
    public static final int SCREEN_WIDTH = TILE_SCALED * SCREEN_COLS; // 768px
    public static final int SCREEN_HEIGHT = TILE_SCALED * SCREEN_ROWS; // 576px
    private static final int FPS = 60;

    // ── Komponen game ──────────────────────────────────────
    private final KeyHandler keyHandler = new KeyHandler();
    private final Camera camera = new Camera();
    private TimeSystem timeSystem;
    private MapManager mapManager;
    private TileMap tileMap;
    private Player player;
    private String  notifText    = "";
    private int     notifTimer   = 0;
    private static final int NOTIF_DURATION = 120; // 2 detik (120 frame)

    private final GameStateManager gsm  = new GameStateManager();
    private final MainMenu         menu = new MainMenu();
    private long menuTick = 0; // untuk animasi menu
    private Store store;
    private boolean nearShop = false;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(keyHandler);

        // Inisialisasi TileMap dengan exception handling
        try {
            tileMap = new TileMap();
        } catch (InvalidMapException e) {
            System.err.println("Gagal memuat map: " + e.getMessage());
            tileMap = new TileMap(true); // fallback map kosong
        }

        mapManager = new MapManager();
        tileMap = mapManager.getCurrentMap();
        player = new Player(keyHandler, tileMap);
        timeSystem = new TimeSystem(tileMap);
        timeSystem.setDayChangeListener((newDay, season) -> {
            player.setStamina(player.getMaxStamina());
            player.setHp(player.getMaxHp());
            store.buildCatalog(season);
        });
        store = new Store(timeSystem.getSeason());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (gsm.is(GameStateManager.State.PLAYING)) {
                SaveManager.save(player, timeSystem, mapManager); // ← ganti tileMap → mapManager
            }
        }));
    }

    /**
     * Mulai game loop dengan javax.swing.Timer — lebih simpel dari Thread manual.
     * Tiap ~16ms: update logika → repaint layar.
     */
    public void startGame() {
        Timer gameTimer = new Timer(1000 / FPS, e -> {
            update();
            repaint();
        });
        gameTimer.start();
    }

    private void update() {
        if (gsm.is(GameStateManager.State.MAIN_MENU)) {
            menuTick++;
            handleMenuInput();

        } else if (gsm.is(GameStateManager.State.PLAYING)) {
            // Cek apakah toko sedang terbuka
            if (store.isOpen()) {
                // ── Toko terbuka → input untuk navigasi toko ──
                keyHandler.tick();
                if (keyHandler.isUpJust())            store.navigateUp();
                if (keyHandler.isDownJust())          store.navigateDown();
                if (keyHandler.isActionJustPressed()) store.buySelected(player);
                if (keyHandler.isShopJustPressed())   store.close(); // B untuk tutup


            } else {
                // ── Toko tertutup → input normal ──
                player.update();

                // Cek proximity ke toko setiap frame
                nearShop = tileMap.isNearShop(player.getX(), player.getY());

                // Buka toko HANYA jika player dekat tile SHOP
                if (nearShop && keyHandler.isShopJustPressed()) {
                    store.open();
                }
                // Cek perpindahan map (entrance)
                if (keyHandler.isEntranceJustPressed()) {
                    TilePos target = player.getFacingTile();
                    if (tileMap.isEntrance(target)) {
                        if (mapManager.getCurrentMapType() == MapManager.MapType.OVERWORLD) {
                            mapManager.enterCave(player);
                        } else {
                            mapManager.exitCave(player);
                        }
                        tileMap = mapManager.getCurrentMap();
                        player.setTileMap(tileMap);
                        timeSystem.setTileMap(tileMap);
                    }
                }
        }
            camera.follow(player, tileMap);
            timeSystem.update();
            if (notifTimer > 0) notifTimer--;
    }
    }

    private void handleMenuInput() {
        if (keyHandler.isUpJust())   menu.navigateUp();
        if (keyHandler.isDownJust()) menu.navigateDown();

        if (keyHandler.isActionJustPressed() || keyHandler.isEntranceJustPressed()) {
            GameStateManager.State next = menu.confirm();
            gsm.setState(next);

            // Jika mulai bermain, cek apakah load save atau new game
            if (next == GameStateManager.State.PLAYING) {
                initGame();
            }
        }

        // Selalu panggil tick agar justPressed terhitung
        keyHandler.tick();
    }

    private void initGame() {
        SaveData data = SaveManager.load();
        if (data.valid) {
            player.loadFromSave(data);
            timeSystem.loadFromSave(data);

            // Load state overworld
            TileMap overworld = mapManager.getOverworldMap();
            if (!data.overworldTiles.isEmpty()) overworld.deserializeTiles(data.overworldTiles);
            if (!data.overworldFarm.isEmpty())  overworld.loadFarmData(data.overworldFarm);

            // Load state cave
            TileMap cave = mapManager.getCaveMap();
            if (!data.caveTiles.isEmpty()) cave.deserializeTiles(data.caveTiles);
            if (!data.caveFarm.isEmpty())  cave.loadFarmData(data.caveFarm);

            // Kembalikan ke map yang aktif saat save
            if ("CAVE".equals(data.currentMap)) {
                // Player save di cave → spawn di entrance cave
                mapManager.setCurrentMap(MapManager.MapType.CAVE);
                tileMap = mapManager.getCaveMap();
            } else {
                // Default → overworld
                mapManager.setCurrentMap(MapManager.MapType.OVERWORLD);
                tileMap = mapManager.getOverworldMap();
            }

            player.setTileMap(tileMap);
            timeSystem.setTileMap(tileMap);

            System.out.println("Melanjutkan game di: " + data.currentMap);
        } else {
            System.out.println("Memulai game baru...");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (gsm.is(GameStateManager.State.MAIN_MENU)) {
            // Hanya render menu
            MenuRenderer.render(g2, menu, menuTick);

        } else if (gsm.is(GameStateManager.State.PLAYING)) {
            // Hanya render game
            tileMap.render(g2, camera);
            player.render(g2, camera.x, camera.y);
            renderHUD(g2);
            InventoryRenderer.renderHotbar(g2, player.getInventory());
            InventoryRenderer.renderBackpack(g2, player.getInventory());
            // Muncul saat player dekat toko dan toko belum terbuka
            if (nearShop && !store.isOpen()) {
                renderShopPrompt(g2);
            }
            StoreRenderer.render(g2, store, player);

            // Notifikasi
            if (notifTimer > 0) {
                float alpha   = Math.min(1f, notifTimer / 30f);
                int   opacity = (int)(alpha * 220);
                g2.setColor(new Color(0, 0, 0, opacity));
                g2.fillRoundRect(SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2 - 20, 200, 36, 10, 10);
                g2.setColor(new Color(255, 220, 60, opacity));
                g2.setFont(new Font("Courier New", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(notifText,
                        SCREEN_WIDTH/2 - fm.stringWidth(notifText)/2,
                        SCREEN_HEIGHT/2 + 4);
            }
        }
        g2.dispose();
    }

    /**
     * Gambar HP bar, Stamina bar, Level, dan Money di pojok kiri atas
     */
    private void renderHUD(Graphics2D g2) {
        // ── PANEL KIRI — stat player ─────────────────────────
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(8, 8, 160, 80, 8, 8);

        // HP bar
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(12, 14, 100, 10, 4, 4);
        g2.setColor(new Color(200, 50, 50));
        int hpW = (int) (100.0 * player.getHp() / player.getMaxHp());
        g2.fillRoundRect(12, 14, hpW, 10, 4, 4);

        // Stamina bar
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(12, 28, 100, 10, 4, 4);
        g2.setColor(new Color(80, 180, 80));
        int stW = (int) (100.0 * player.getStamina() / player.getMaxStamina());
        g2.fillRoundRect(12, 28, stW, 10, 4, 4);

        // Label HP & Stamina
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Courier New", Font.PLAIN, 9));
        g2.drawString("HP", 116, 22);
        g2.drawString("ST", 116, 36);

        // Level & uang
        g2.setFont(new Font("Courier New", Font.BOLD, 10));
        g2.setColor(new Color(255, 220, 60));
        g2.drawString("Lv." + player.getLevel() + "   $" + player.getMoney(), 12, 52);

        // Nama item aktif di slot
        g2.setColor(new Color(200, 200, 200));
        g2.setFont(new Font("Courier New", Font.PLAIN, 9));
        if (player.getInventory().getActiveItem() != null) {
            Item active = player.getInventory().getActiveItem();
            String extra = "";
            if (active instanceof Tool) {
                extra = " (" + ((Tool) active).getTierName() + ")";
            }
            g2.drawString(active.getName() + extra, 12, 66);
        }


        // Petunjuk tombol jual
        g2.setColor(new Color(160, 160, 160));
        g2.drawString("[F] Jual item aktif", 12, 78);


        // ── PANEL KANAN — info waktu & hari ──────────────────

        // Lebar panel kanan 130px, di pojok kanan atas
        int panelW = 130;
        int panelX = GamePanel.SCREEN_WIDTH - panelW - 8;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(panelX, 8, panelW, 88, 8, 8);

        // ── Ikon musim (warna kotak kecil) ──────────────────
        Color seasonColor = switch (timeSystem.getSeason()) {
            case SPRING -> new Color(120, 210, 100); // hijau muda
            case SUMMER -> new Color(240, 160, 40); // oranye
            case FALL -> new Color(200, 90, 40); // coklat merah
            case WINTER -> new Color(140, 180, 220); // biru muda
        };
        g2.setColor(seasonColor);
        g2.fillRoundRect(panelX + 8, 16, 10, 10, 3, 3);

        // Nama musim
        g2.setFont(new Font("Courier New", Font.BOLD, 10));
        g2.setColor(seasonColor);
        g2.drawString(timeSystem.getSeason().getDisplayName(), panelX + 22, 24);

        // Hari
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Courier New", Font.BOLD, 11));
        g2.drawString("Day " + timeSystem.getDay(), panelX + 8, 42);

        // Jam
        g2.setFont(new Font("Courier New", Font.PLAIN, 10));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(timeSystem.getTimeString(), panelX + 8, 56);

        // ── Progress bar waktu (siang → malam) ───────────────

        // Latar bar
        int barX = panelX + 8;
        int barW = panelW - 16;
        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRoundRect(barX, 64, barW, 8, 4, 4);

        // Isi bar — warnanya berubah sesuai waktu
        float progress = timeSystem.getDayProgress();
        // Pagi = kuning, sore = oranye, malam = biru gelap
        Color barColor;
        if (progress < 0.5f) {
            barColor = new Color(255, 200, 60);  // siang
        } else if (progress < 0.8f) {
            barColor = new Color(220, 120, 40);  // sore
        } else {
            barColor = new Color(80, 80, 160);   // malam
        }
        g2.setColor(barColor);
        g2.fillRoundRect(barX, 64, (int) (barW * progress), 8, 4, 4);

        // Label kiri kanan bar (pagi / malam)
        g2.setFont(new Font("Courier New", Font.PLAIN, 8));
        g2.setColor(new Color(160, 160, 160));
        g2.drawString("06:00", barX, 82);
        g2.drawString("22:00", barX + barW - 28, 82);
    }

    private void renderShopPrompt(Graphics2D g2) {
        String text = "[B] Belanja";

        // Hitung posisi di atas kepala player
        int screenX = player.getX() - camera.x;
        int screenY = player.getY() - camera.y;

        // Background prompt
        g2.setFont(new Font("Courier New", Font.BOLD, 10));
        FontMetrics fm  = g2.getFontMetrics();
        int         tw  = fm.stringWidth(text);
        int         px  = screenX + GamePanel.TILE_SCALED / 2 - tw / 2 - 6;
        int         py  = screenY - 24;
        int         pw  = tw + 12;

        // Clamp agar tidak keluar layar
        px = Math.max(4, Math.min(px, SCREEN_WIDTH - pw - 4));
        py = Math.max(4, py);

        // Background gelap semi-transparan
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(px, py, pw, 16, 6, 6);

        // Border kuning
        g2.setColor(new Color(255, 220, 60, 200));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(px, py, pw, 16, 6, 6);

        // Teks
        g2.setColor(new Color(255, 240, 160));
        g2.drawString(text, px + 6, py + 12);
    }

}
