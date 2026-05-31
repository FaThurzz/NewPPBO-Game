package game.store;
import game.entity.Player;
import game.items.*;
import game.world.Season;

import java.util.ArrayList;
import java.util.List;

/**
 * Toko tempat player membeli item.
 * Konsep OOP: Encapsulation
 * Katalog toko berubah berdasarkan musim —
 * benih yang dijual berbeda tiap musim.
 */
public class Store {

    private final List<StoreItem> catalog = new ArrayList<>();
    private int selectedIndex = 0;
    private boolean open      = false;

    // Konstruktor — isi katalog awal
    public Store(Season currentSeason) {
        buildCatalog(currentSeason);
    }

    /**
     * Bangun katalog berdasarkan musim saat ini.
     * Dipanggil ulang saat musim berganti.
     */
    public void buildCatalog(Season season) {
        catalog.clear();

        // ── Menu (selalu tersedia) ────────────────────────
        catalog.add(new StoreItem(Seed.stroberi(), 20,  "Seeds"));
        catalog.add(new StoreItem(Seed.carrot(),   50,  "Seeds"));
        catalog.add(new StoreItem(Seed.potato(),   80,  "Seeds"));
        catalog.add(new StoreItem(
                new Food("Fried Egg", "Telur goreng.",
                        50, 30, null, 0, 35, 0),
                50, "Food"
        ));
        catalog.add(new StoreItem(
                new Food("Salad", "Menyegarkan.",
                        20, 60, "speed", 5, 80, 0),
                80, "Food"
        ));
    }

    /**
     * Player membeli item yang sedang dipilih.
     * Membeli 1 item per kali tekan.
     * Return true jika berhasil.
     */
    public boolean buySelected(Player player) {
        if (catalog.isEmpty()) return false;

        StoreItem storeItem = catalog.get(selectedIndex);
        int price = storeItem.getPrice();

        // Cek uang cukup
        if (player.getMoney() < price) {
            System.out.println("Uang tidak cukup! Butuh $" + price);
            return false;
        }

        // Buat salinan item baru untuk ditambahkan ke inventory
        // (agar item di katalog tidak termodifikasi)
        Item copy = copyItem(storeItem.getItem());
        if (copy == null) return false;

        // Cek inventory tidak penuh
        boolean masuk = player.getInventory().addItem(copy);
        if (!masuk) {
            System.out.println("Inventory penuh!");
            return false;
        }

        // Kurangi uang
        player.addMoney(-price);
        System.out.println("Beli " + copy.getName() + " seharga $" + price
                + " | Sisa: $" + player.getMoney());
        return true;
    }

    /**
     * Buat salinan item agar setiap pembelian
     * menghasilkan objek baru yang independen.
     */
    private Item copyItem(Item original) {
        if (original instanceof Seed s) {
            return switch (s.getCropType()) {
                case "Stroberi" -> Seed.stroberi();
                case "Carrot"   -> Seed.carrot();
                case "Potato"   -> Seed.potato();
                default         -> null;
            };
        }
        if (original instanceof Food f) {
            return new Food(
                    f.getName(), f.getDescription(),
                    f.getHpRestore(),       // ← perlu tambah getter ini
                    f.getStaminaRestore(),  // ← perlu tambah getter ini
                    f.getBuffType(),        // ← perlu tambah getter ini
                    f.getBuffDuration(),    // ← perlu tambah getter ini
                    f.getSellPrice(),
                    f.getBuyPrice()
            );
        }
        return null;
    }

    // ── Navigasi ──────────────────────────────────────────

    public void navigateUp() {
        selectedIndex = (selectedIndex - 1 + catalog.size()) % catalog.size();
    }

    public void navigateDown() {
        selectedIndex = (selectedIndex + 1) % catalog.size();
    }

    // ── Buka/tutup ────────────────────────────────────────

    public void open()  { open = true;  selectedIndex = 0; }
    public void close() { open = false; }
    public boolean isOpen() { return open; }

    // ── Getters ───────────────────────────────────────────

    public List<StoreItem> getCatalog()      { return catalog; }
    public int             getSelectedIndex(){ return selectedIndex; }
    public StoreItem       getSelected() {
        if (catalog.isEmpty()) return null;
        return catalog.get(selectedIndex);
    }
}