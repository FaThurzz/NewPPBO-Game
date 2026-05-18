package game.engine;

import game.entity.Player;
import game.exception.InvalidMapException;
import game.items.InventoryRenderer;
import game.world.Camera;
import game.world.TileMap;
import game.world.TimeSystem;

import javax.swing.*;
import java.awt.*;

/**
 * GAME PANEL — koordinator utama
 * Konsep OOP: Encapsulation
 * Merge:
 * - Game loop pakai javax.swing.Timer (dari versi simplified — lebih mudah dipahami)
 * - Camera dari world.Camera (dari versi temanmu)
 * - InventoryRenderer hotbar (dari versi temanmu)
 * - HUD HP + Stamina + Level + Money (gabungan keduanya)
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
    private TileMap tileMap;
    private Player player;
    private String  notifText    = "";
    private int     notifTimer   = 0;
    private static final int NOTIF_DURATION = 120; // 2 detik (120 frame)

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
        timeSystem = new TimeSystem(tileMap);
        timeSystem.setDayChangeListener((newDay, season) -> {
            // Stamina & HP pulih penuh setiap pagi
            // Math.min memastikan tidak melebihi nilai maksimum
            player.setStamina(player.getMaxStamina());
            player.setHp(player.getMaxHp());
            System.out.println("Pagi hari! Stamina & HP pulih penuh.");
            System.out.println("Day " + newDay + " | " + season.getDisplayName());
        });
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
        timeSystem.update();
        if (notifTimer > 0) notifTimer--;
    }

    /**
     * Tampilkan teks notifikasi di tengah layar selama 2 detik.
     * Timer dihitung mundur tiap frame, notif hilang saat = 0.
     */
    public void showNotif(String text) {
        notifText  = text;
        notifTimer = NOTIF_DURATION;
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

        if (notifTimer > 0) {
            // Hitung opacity: mulai solid, makin transparan menjelang habis
            // notifTimer/NOTIF_DURATION menghasilkan 1.0 → 0.0
            float alpha = Math.min(1f, notifTimer / 30f); // fade out di 30 frame terakhir
            int   opacity = (int)(alpha * 220);

            g2.setColor(new Color(0, 0, 0, opacity));
            g2.fillRoundRect(
                    GamePanel.SCREEN_WIDTH / 2 - 100,
                    GamePanel.SCREEN_HEIGHT / 2 - 20,
                    200, 36, 10, 10
            );
            g2.setColor(new Color(255, 220, 60, opacity));
            g2.setFont(new Font("Courier New", Font.BOLD, 11));

            // Center teks
            FontMetrics fm = g2.getFontMetrics();
            int textX = GamePanel.SCREEN_WIDTH  / 2 - fm.stringWidth(notifText) / 2;
            int textY = GamePanel.SCREEN_HEIGHT / 2 + 4;
            g2.drawString(notifText, textX, textY);
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
            g2.drawString(player.getInventory().getActiveItem().getName(), 12, 66);
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
}
