package game.menu;

import game.engine.GamePanel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Menggambar tampilan menu utama.
 * Konsep OOP: Encapsulation (semua method static)
 */
public class MenuRenderer {

    public static void render(Graphics2D g, MainMenu menu, long tick) {
        int W = GamePanel.SCREEN_WIDTH;
        int H = GamePanel.SCREEN_HEIGHT;

        // ── Background gradasi gelap ──────────────────────
        GradientPaint bg = new GradientPaint(
                0, 0,   new Color(10, 30, 15),   // atas: hijau sangat gelap
                0, H,   new Color(5,  15, 30)    // bawah: biru tua
        );
        g.setPaint(bg);
        g.fillRect(0, 0, W, H);

        // ── Bintang-bintang kecil (dekorasi) ─────────────
        drawStars(g, W, H, tick);

        // ── Judul game ───────────────────────────────────
        drawTitle(g, W, tick);

        // ── Panel menu ───────────────────────────────────
        drawMenuPanel(g, menu, W, H);

        // ── Footer ───────────────────────────────────────
        g.setColor(new Color(100, 100, 100));
        g.setFont(new Font("Courier New", Font.PLAIN, 9));
        g.drawString("↑↓ Navigasi   Enter / Z Pilih", W / 2 - 80, H - 10);
    }

    // ── Judul dengan efek "berkedip" ──────────────────────
    private static void drawTitle(Graphics2D g, int W, long tick) {
        // Shadow judul
        g.setFont(new Font("Courier New", Font.BOLD, 36));
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString("Meadow Tales", W / 2 - 142, 122);

        // Judul utama dengan warna beranimasi
        float hue = (tick % 300) / 300f; // warna berputar pelan
        Color titleColor = Color.getHSBColor(0.35f + hue * 0.05f, 0.6f, 1.0f);
        g.setColor(titleColor);
        g.drawString("Meadow Tales", W / 2 - 140, 120);

        // Subtitle
        g.setFont(new Font("Courier New", Font.ITALIC, 12));
        g.setColor(new Color(150, 200, 150));
        g.drawString("~ A Simple Farming Adventure ~", W / 2 - 110, 145);
    }

    // ── Panel pilihan menu ────────────────────────────────
    private static void drawMenuPanel(Graphics2D g, MainMenu menu, int W, int H) {
        int panelW = 220;
        int panelH = 140;
        int panelX = W / 2 - panelW / 2;
        int panelY = H / 2 - 20;

        // Background panel
        g.setColor(new Color(10, 25, 15, 200));
        g.fill(new RoundRectangle2D.Float(panelX, panelY, panelW, panelH, 12, 12));

        // Border panel
        g.setColor(new Color(80, 140, 80, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new RoundRectangle2D.Float(panelX, panelY, panelW, panelH, 12, 12));
        g.setStroke(new BasicStroke(1f));

        // Render tiap pilihan
        String[] labels   = {"New Game", "Continue", "Quit"};
        boolean[] enabled = {
                true,               // New Game selalu aktif
                menu.hasSave(),     // Continue hanya aktif jika ada save
                true                // Quit selalu aktif
        };

        for (int i = 0; i < labels.length; i++) {
            int optX = panelX + 20;
            int optY = panelY + 35 + i * 38;
            boolean isSelected = (i == menu.getSelectedIndex());
            boolean isEnabled  = enabled[i];

            if (isSelected && isEnabled) {
                // Background highlight pilihan aktif
                g.setColor(new Color(80, 160, 80, 100));
                g.fillRoundRect(optX - 10, optY - 16, panelW - 20, 28, 8, 8);

                // Tanda "►" di kiri
                g.setColor(new Color(150, 255, 150));
                g.setFont(new Font("Courier New", Font.BOLD, 13));
                g.drawString("►", optX - 6, optY + 2);
            }

            // Warna teks: terang jika aktif & terpilih, abu jika disabled
            if (!isEnabled) {
                g.setColor(new Color(80, 80, 80)); // abu — tidak tersedia
            } else if (isSelected) {
                g.setColor(new Color(200, 255, 200)); // hijau terang — dipilih
            } else {
                g.setColor(new Color(160, 200, 160)); // hijau muda — normal
            }

            g.setFont(new Font("Courier New", Font.BOLD, 14));
            g.drawString(labels[i], optX + 14, optY + 2);

            // Label "(No Save)" di sebelah Continue jika disabled
            if (i == 1 && !isEnabled) {
                g.setColor(new Color(80, 80, 80));
                g.setFont(new Font("Courier New", Font.PLAIN, 9));
                g.drawString("(no save)", optX + 90, optY + 2);
            }
        }
    }

    // ── Bintang dekorasi ──────────────────────────────────
    private static void drawStars(Graphics2D g, int W, int H, long tick) {
        // Posisi bintang ditentukan dari angka tetap agar tidak berubah tiap frame
        int[][] stars = {
                {50, 40}, {120, 80}, {200, 30}, {300, 60}, {420, 25},
                {500, 70}, {600, 45}, {700, 55}, {150, 200}, {650, 180},
                {80, 300}, {720, 350}, {350, 400}, {600, 420}, {100, 450}
        };

        for (int[] s : stars) {
            // Kedip dengan offset berbeda tiap bintang
            float alpha = 0.3f + 0.4f * (float) Math.sin(tick * 0.03 + s[0]);
            g.setColor(new Color(1f, 1f, 1f, Math.max(0f, Math.min(1f, alpha))));
            g.fillOval(s[0], s[1], 2, 2);
        }
    }
}