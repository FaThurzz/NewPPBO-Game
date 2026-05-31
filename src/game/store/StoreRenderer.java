package game.store;

import game.engine.GamePanel;
import game.entity.Player;
import game.items.ItemType;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StoreRenderer {

    private static final int PANEL_W    = 400;
    private static final int PANEL_H    = 380;
    private static final int ROW_H      = 44;
    private static final int ICON_SIZE  = 28;
    private static final int PADDING    = 16;

    public static void render(Graphics2D g, Store store, Player player) {
        if (!store.isOpen()) return;

        int panelX = (GamePanel.SCREEN_WIDTH  - PANEL_W) / 2;
        int panelY = (GamePanel.SCREEN_HEIGHT - PANEL_H) / 2;

        g.setColor(new Color(20, 15, 10, 235));
        g.fill(new RoundRectangle2D.Float(panelX, panelY, PANEL_W, PANEL_H, 14, 14));

        g.setColor(new Color(180, 140, 60));
        g.setStroke(new BasicStroke(2f));
        g.draw(new RoundRectangle2D.Float(panelX, panelY, PANEL_W, PANEL_H, 14, 14));
        g.setStroke(new BasicStroke(1f));

        g.setColor(new Color(255, 220, 60));
        g.setFont(new Font("Courier New", Font.BOLD, 14));
        g.drawString("🏪  Pierre's General Store", panelX + PADDING, panelY + 24);

        String moneyStr = "$" + player.getMoney();
        g.setFont(new Font("Courier New", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(255, 220, 60));
        g.drawString(moneyStr, panelX + PANEL_W - fm.stringWidth(moneyStr) - PADDING, panelY + 24);

        g.setColor(new Color(180, 140, 60, 100));
        g.drawLine(panelX + PADDING, panelY + 32, panelX + PANEL_W - PADDING, panelY + 32);

        int listY    = panelY + 44;
        int maxItems = (PANEL_H - 80) / ROW_H;
        var catalog  = store.getCatalog();

        int selected = store.getSelectedIndex();
        int startIdx = Math.max(0, Math.min(selected - maxItems / 2,
                catalog.size() - maxItems));
        int endIdx   = Math.min(startIdx + maxItems, catalog.size());

        for (int i = startIdx; i < endIdx; i++) {
            StoreItem si       = catalog.get(i);
            boolean isSelected = (i == selected);
            int rowY = listY + (i - startIdx) * ROW_H;

            if (isSelected) {
                g.setColor(new Color(180, 140, 60, 60));
                g.fillRoundRect(panelX + 8, rowY + 2, PANEL_W - 16, ROW_H - 4, 8, 8);
                g.setColor(new Color(255, 220, 60, 120));
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(panelX + 8, rowY + 2, PANEL_W - 16, ROW_H - 4, 8, 8);
            }

            // ── Icon: gambar jika ada, fallback warna+huruf ──
            if (si.getItem().getIcon() != null) {
                g.drawImage(si.getItem().getIcon(),
                        panelX + PADDING, rowY + 8,
                        ICON_SIZE, ICON_SIZE, null);
            } else {
                Color iconColor = colorOf(si.getItem().getType());
                g.setColor(iconColor);
                g.fillRoundRect(panelX + PADDING, rowY + 8, ICON_SIZE, ICON_SIZE, 6, 6);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Courier New", Font.BOLD, 12));
                g.drawString(
                        String.valueOf(si.getItem().getName().charAt(0)),
                        panelX + PADDING + 8, rowY + 26
                );
            }

            g.setColor(isSelected ? new Color(255, 240, 180) : new Color(200, 200, 200));
            g.setFont(new Font("Courier New", Font.BOLD, 11));
            g.drawString(si.getItem().getName(), panelX + PADDING + ICON_SIZE + 10, rowY + 20);

            g.setColor(new Color(130, 130, 130));
            g.setFont(new Font("Courier New", Font.PLAIN, 9));
            g.drawString(si.getCategory(), panelX + PADDING + ICON_SIZE + 10, rowY + 34);

            String priceStr = "$" + si.getPrice();
            g.setFont(new Font("Courier New", Font.BOLD, 11));
            boolean cukup = player.getMoney() >= si.getPrice();
            g.setColor(cukup ? new Color(100, 220, 100) : new Color(200, 80, 80));
            FontMetrics pfm = g.getFontMetrics();
            g.drawString(priceStr,
                    panelX + PANEL_W - pfm.stringWidth(priceStr) - PADDING,
                    rowY + 26
            );
        }

        if (catalog.size() > maxItems) {
            int sbH    = PANEL_H - 90;
            int sbX    = panelX + PANEL_W - 8;
            int thumbH = Math.max(20, sbH * maxItems / catalog.size());
            int thumbY = listY + (sbH - thumbH) * selected / Math.max(1, catalog.size() - 1);

            g.setColor(new Color(80, 80, 80, 120));
            g.fillRoundRect(sbX, listY, 4, sbH, 2, 2);
            g.setColor(new Color(180, 140, 60, 180));
            g.fillRoundRect(sbX, thumbY, 4, thumbH, 2, 2);
        }

        int footY = panelY + PANEL_H - 12;
        g.setColor(new Color(120, 120, 120));
        g.setFont(new Font("Courier New", Font.PLAIN, 9));
        g.drawString("[↑↓] Navigasi   [Space/E] Beli   [B] Tutup",
                panelX + PADDING, footY);

        StoreItem sel = store.getSelected();
        if (sel != null) {
            g.setColor(new Color(160, 160, 100));
            g.setFont(new Font("Courier New", Font.PLAIN, 9));
            g.drawString(sel.getItem().getDescription(),
                    panelX + PADDING, footY - 14);
        }
    }

    private static Color colorOf(ItemType type) {
        return switch (type) {
            case SEED     -> new Color(80,  180, 80);
            case CROP     -> new Color(220, 150, 40);
            case TOOL     -> new Color(120, 140, 200);
            case FOOD     -> new Color(200,  80, 80);
            case MATERIAL -> new Color(140, 110, 70);
            default       -> new Color(150, 150, 150);
        };
    }
}