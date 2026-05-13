package game.items;

/**
 * INVENTORY — hotbar 9 slot untuk menyimpan item
 * Konsep OOP: Encapsulation (slot private, diakses lewat method)
 */
public class Inventory {

    public static final int HOTBAR_SIZE = 9;

    private final Item[] hotbar = new Item[HOTBAR_SIZE];
    private int activeIndex = 0;

    /**
     * Tambahkan item. Coba stack ke slot sejenis dulu,
     * kalau tidak ada cari slot kosong.
     * Return true jika berhasil.
     */
    public boolean addItem(Item incoming) {
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (hotbar[i] != null && hotbar[i].canStackWith(incoming)) {
                hotbar[i].quantity += incoming.quantity;
                return true;
            }
        }
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (hotbar[i] == null) { hotbar[i] = incoming; return true; }
        }
        System.out.println("Inventory penuh!");
        return false;
    }

    /** Kurangi quantity item di slot, hapus jika habis */
    public void consumeAt(int index) {
        if (hotbar[index] == null) return;
        hotbar[index].quantity--;
        if (hotbar[index].quantity <= 0) hotbar[index] = null;
    }

    public void removeAt(int index)    { hotbar[index] = null; }

    // ── Akses slot ────────────────────────────────────────
    public Item getActiveItem()        { return hotbar[activeIndex]; }
    public Item getItem(int i)         { return (i >= 0 && i < HOTBAR_SIZE) ? hotbar[i] : null; }

    // ── Navigasi ──────────────────────────────────────────
    public void setActiveIndex(int i)  { if (i >= 0 && i < HOTBAR_SIZE) activeIndex = i; }
    public void scrollNext()           { activeIndex = (activeIndex + 1) % HOTBAR_SIZE; }
    public void scrollPrev()           { activeIndex = (activeIndex - 1 + HOTBAR_SIZE) % HOTBAR_SIZE; }
    public int  getActiveIndex()       { return activeIndex; }
}
