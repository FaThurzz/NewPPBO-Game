package game.engine;

import game.entity.Player;
import game.exception.InvalidMapException;
import game.items.InventoryRenderer;
import game.world.Camera;
import game.world.TileMap;

import javax.swing.*;
import java.awt.*;

/**
 * GAME PANEL — koordinator utama
 *
 * Konsep OOP: Encapsulation
 * Merge:
 * - Game loop pakai javax.swing.Timer (dari versi simplified — lebih mudah dipahami)
 * - Camera dari world.Camera (dari versi temanmu)
 * - InventoryRenderer hotbar (dari versi temanmu)
 * - HUD HP + Stamina + Level + Money (gabungan keduanya)
 */
public class GamePanel extends JPanel {

    // ── Konstanta layar ────────────────────────────────────
    public static final int TILE_SIZE     = 16;
    public static final int SCALE         = 3;
    public static final int TILE_SCALED   = TILE_SIZE * SCALE; // 48px
    public static final int SCREEN_COLS   = 16;
    public static final int SCREEN_ROWS   = 12;
    public static final int SCREEN_WIDTH  = TILE_SCALED * SCREEN_COLS; // 768px
    public static final int SCREEN_HEIGHT = TILE_SCALED * SCREEN_ROWS; // 576px
    private static final int FPS          = 60;

    // ── Komponen game ──────────────────────────────────────
    private final KeyHandler keyHandler = new KeyHandler();
    private final Camera     camera     = new Camera();
    private TileMap tileMap;
    private Player  player;

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

        player = new Player(keyHandler, tileMap);
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
        player.update();
        camera.follow(player, tileMap);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Nearest-neighbor scaling agar pixel art tidak blur
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        // 1. Render peta
        tileMap.render(g2, camera);

        // 2. Render player (Polymorphism: render() via interface Renderable)
        player.render(g2, camera.x, camera.y);

        // 3. HUD
        renderHUD(g2);

        // 4. Hotbar inventory (dari versi temanmu)
        InventoryRenderer.renderHotbar(g2, player.getInventory());

        g2.dispose();
    }

    /** Gambar HP bar, Stamina bar, Level, dan Money di pojok kiri atas */
    private void renderHUD(Graphics2D g2) {
        // Background transparan
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(8, 8, 160, 70, 8, 8);

        // ── HP bar ────────────────────────────────────────
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(12, 14, 100, 10, 4, 4);
        g2.setColor(new Color(200, 50, 50));
        int hpW = (int)(100.0 * player.getHp() / player.getMaxHp());
        g2.fillRoundRect(12, 14, hpW, 10, 4, 4);

        // ── Stamina bar ───────────────────────────────────
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(12, 28, 100, 10, 4, 4);
        g2.setColor(new Color(80, 180, 80));
        int stW = (int)(100.0 * player.getStamina() / player.getMaxStamina());
        g2.fillRoundRect(12, 28, stW, 10, 4, 4);

        // ── Teks info ─────────────────────────────────────
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Courier New", Font.PLAIN, 9));
        g2.drawString("HP  " + player.getHp() + "/" + player.getMaxHp(),       118, 22);
        g2.drawString("ST  " + player.getStamina() + "/" + player.getMaxStamina(), 118, 36);

        g2.setFont(new Font("Courier New", Font.BOLD, 10));
        g2.setColor(new Color(255, 220, 60));
        g2.drawString("Lv." + player.getLevel() + "   $" + player.getMoney(), 12, 52);

        // ── Item aktif ────────────────────────────────────
        if (player.getInventory().getActiveItem() != null) {
            g2.setColor(new Color(200, 200, 200));
            g2.setFont(new Font("Courier New", Font.PLAIN, 9));
            g2.drawString(player.getInventory().getActiveItem().getName(), 12, 65);
        }
    }
}
