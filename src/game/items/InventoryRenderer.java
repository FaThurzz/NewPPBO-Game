package game.items;

import game.engine.GamePanel;

import java.awt.*;

/**
 * INVENTORY RENDERER — menggambar hotbar di bagian bawah layar
 * Konsep OOP: Encapsulation (semua method static, tidak perlu instansiasi)
 */
public class InventoryRenderer {

    private static final int SLOT_SIZE   = 40;
    private static final int SLOT_MARGIN = 4;
    private static final int PADDING     = 6;

    /** Dipanggil dari GamePanel.paintComponent() */
    public static void renderHotbar(Graphics2D g, Inventory inv) {
        int total    = Inventory.HOTBAR_SIZE;
        int barWidth = total * (SLOT_SIZE + SLOT_MARGIN) - SLOT_MARGIN;
        int startX   = (GamePanel.SCREEN_WIDTH - barWidth) / 2;
        int startY   = GamePanel.SCREEN_HEIGHT - SLOT_SIZE - 12;

        for (int i = 0; i < total; i++) {
            int     slotX    = startX + i * (SLOT_SIZE + SLOT_MARGIN);
            boolean isActive = (i == inv.getActiveIndex());

            drawSlot(g, slotX, startY, isActive);

            Item item = inv.getItem(i);
            if (item != null) drawItemInSlot(g, item, slotX, startY);
        }
    }

    private static void drawSlot(Graphics2D g, int x, int y, boolean active) {
        g.setColor(new Color(40, 30, 20, 200));
        g.fillRoundRect(x, y, SLOT_SIZE, SLOT_SIZE, 6, 6);

        g.setColor(active ? new Color(255, 220, 60) : new Color(100, 90, 80));
        g.setStroke(new BasicStroke(active ? 2.5f : 1.5f));
        g.drawRoundRect(x, y, SLOT_SIZE, SLOT_SIZE, 6, 6);
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawItemInSlot(Graphics2D g, Item item, int x, int y) {
        if (item.getIcon() != null) {
            g.drawImage(item.getIcon(),
                x + PADDING, y + PADDING,
                SLOT_SIZE - PADDING * 2, SLOT_SIZE - PADDING * 2,
                null);
        } else {
            // Fallback: blok warna + huruf pertama nama item
            g.setColor(colorOf(item.getType()));
            g.fillRoundRect(x + PADDING, y + PADDING,
                SLOT_SIZE - PADDING * 2, SLOT_SIZE - PADDING * 2, 4, 4);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.BOLD, 11));
            g.drawString(String.valueOf(item.getName().charAt(0)),
                x + SLOT_SIZE / 2 - 4, y + SLOT_SIZE / 2 + 4);
        }

        // Quantity di pojok kanan bawah
        if (item.getQuantity() > 1) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier New", Font.BOLD, 9));
            String qty = String.valueOf(item.getQuantity());
            g.drawString(qty, x + SLOT_SIZE - 6 - qty.length() * 5, y + SLOT_SIZE - 4);
        }
    }
    public static void renderBackpack(Graphics2D g, Inventory inv) {
        if (!inv.isBackpackOpen()) return; // tidak digambar kalau tertutup

        int cols    = 9;
        int rows    = 3;
        int panelW  = cols * (SLOT_SIZE + SLOT_MARGIN) - SLOT_MARGIN + 20;
        int panelH  = rows * (SLOT_SIZE + SLOT_MARGIN) - SLOT_MARGIN + 30;
        int panelX  = (GamePanel.SCREEN_WIDTH  - panelW) / 2;
        int panelY  = (GamePanel.SCREEN_HEIGHT - panelH) / 2 - 20; // tengah layar

        // Background panel
        g.setColor(new Color(30, 20, 10, 220));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 12, 12);

        // Border
        g.setColor(new Color(180, 140, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 12, 12);
        g.setStroke(new BasicStroke(1f));

        // Judul + petunjuk tombol
        g.setColor(new Color(255, 220, 60));
        g.setFont(new Font("Courier New", Font.BOLD, 11));
        g.drawString("Backpack", panelX + 10, panelY + 16);
        g.setColor(new Color(160, 160, 160));
        g.setFont(new Font("Courier New", Font.PLAIN, 9));
        g.drawString("[1-9] Pilih Kolom  [↑↓] Pindah Baris  [Z] Pakai  [F] Jual  [I] Tutup",
                panelX + 10, panelY + panelH - 6);

        // Render tiap slot backpack
        int activeRow = inv.getBackpackActiveRow();
        int activeCol = inv.getBackpackActiveCol();
        for (int i = 0; i < Inventory.BACKPACK_SIZE; i++) {
            int col   = i % cols;
            int row   = i / cols;
            int slotX = panelX + 10 + col * (SLOT_SIZE + SLOT_MARGIN);
            int slotY = panelY + 22 + row * (SLOT_SIZE + SLOT_MARGIN);
            boolean isActive = (col == activeCol && row == activeRow);
            drawSlot(g, slotX, slotY, false);
            // Highlight seluruh baris aktif dengan warna berbeda
            if (row == activeRow && !isActive) {
                g.setColor(new Color(255, 220, 60, 30)); // kuning transparan
                g.fillRoundRect(slotX + 1, slotY + 1,
                        SLOT_SIZE - 2, SLOT_SIZE - 2, 4, 4);
            }
            Item item = inv.getBackpackItem(i);
            if (item != null) drawItemInSlot(g, item, slotX, slotY);
        }
        int arrowY = panelY + 22 + activeRow * (SLOT_SIZE + SLOT_MARGIN)
                + SLOT_SIZE / 2;
        g.setColor(new Color(255, 220, 60));
        g.setFont(new Font("Courier New", Font.BOLD, 12));
        g.drawString("►", panelX - 2, arrowY + 4);
    }
    private static Color colorOf(ItemType type) {
        return switch (type) {
            case SEED     -> new Color(120, 200, 80);
            case CROP     -> new Color(255, 180, 50);
            case TOOL     -> new Color(150, 150, 200);
            case FOOD     -> new Color(220, 100, 100);
            case MATERIAL -> new Color(160, 130, 90);
            default       -> new Color(180, 180, 180);
        };
    }
}
