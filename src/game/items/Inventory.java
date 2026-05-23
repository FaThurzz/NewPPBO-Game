package game.items;

/**
 * INVENTORY — hotbar 9 slot untuk menyimpan item
 * Konsep OOP: Encapsulation (slot private, diakses lewat method)
 */
public class Inventory {

    public static final int HOTBAR_SIZE = 9;
    public static final int BACKPACK_SIZE = 27;

    private final Item[] hotbar = new Item[HOTBAR_SIZE];
    private final Item[] backpack = new Item[BACKPACK_SIZE];
    private int activeIndex = 0;
    private boolean backpackOpen = false;

    private int backpackActiveRow = 0; // baris yang sedang aktif (0, 1, atau 2)
    private int backpackActiveCol = 0; // kolom yang sedang aktif (0-8)

    /**
     * Tambahkan item. Coba stack ke slot sejenis dulu,
     * kalau tidak ada cari slot kosong.
     * Return true jika berhasil.
     */
    public boolean addItem(Item incoming) {
        // Coba stack di hotbar
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (hotbar[i] != null && hotbar[i].canStackWith(incoming)) {
                hotbar[i].quantity += incoming.quantity;
                return true;
            }
        }
        // Coba stack di backpack
        for (int i = 0; i < BACKPACK_SIZE; i++) {
            if (backpack[i] != null && backpack[i].canStackWith(incoming)) {
                backpack[i].quantity += incoming.quantity;
                return true;
            }
        }
        // Cari slot kosong di hotbar
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            if (hotbar[i] == null) { hotbar[i] = incoming; return true; }
        }
        // Cari slot kosong di backpack
        for (int i = 0; i < BACKPACK_SIZE; i++) {
            if (backpack[i] == null) { backpack[i] = incoming; return true; }
        }
        System.out.println("Inventory penuh!");
        return false;
    }

    private void compactArray(Item[] arr, int size) {
        Item[] hasil = new Item[size];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            if (arr[i] != null) hasil[idx++] = arr[i];
        }
        System.arraycopy(hasil, 0, arr, 0, size);
    }

    /** Kurangi quantity item di slot, hapus jika habis */
    public void consumeAt(int index) {
        if (hotbar[index] == null) return;
        hotbar[index].quantity--;
        if (hotbar[index].quantity <= 0) {
            hotbar[index] = null;
            refillHotbarFromBackpack();
            compact();
        }
    }

    public void compact() {
        compactArray(hotbar,   HOTBAR_SIZE);
        compactArray(backpack, BACKPACK_SIZE);
        // Hitung jumlah item yang tersisa di hotbar
        // untuk memastikan activeIndex tidak keluar batas
        int isiHotbar = 0;
        for (Item item : hotbar) {
            if (item != null) isiHotbar++;
        }
        if (activeIndex >= isiHotbar && isiHotbar > 0) {
            activeIndex = isiHotbar - 1;
        }
    }
    public void removeAt(int index){
        hotbar[index] = null;
        refillHotbarFromBackpack();
        compact();
    }
    public void removeBackpackAt(int i) {
        if (i >= 0 && i < BACKPACK_SIZE) backpack[i] = null;
        compact();
    }
    public void backpackRowUp() {
        backpackActiveRow = Math.max(0, backpackActiveRow - 1);
    }
    public void backpackRowDown() {
        backpackActiveRow = Math.min(2, backpackActiveRow + 1); // max 2 (3 baris)
    }
    // Set kolom aktif backpack via tombol 1-9:
    public void setBackpackActiveCol(int col) {
        if (col >= 0 && col < 9) backpackActiveCol = col;
    }
    // Hitung index slot backpack yang aktif:
    // baris × 9 kolom + kolom aktif
    public int getBackpackActiveIndex() {
        return backpackActiveRow * 9 + backpackActiveCol;
    }
    // Ambil item di slot backpack yang aktif:
    public Item getBackpackActiveItem() {
        return getBackpackItem(getBackpackActiveIndex());
    }
    private void refillHotbarFromBackpack() {
        for (int h = 0; h < HOTBAR_SIZE; h++) {
            if (hotbar[h] == null) {
                // Cari item pertama yang ada di backpack
                for (int b = 0; b < BACKPACK_SIZE; b++) {
                    if (backpack[b] != null) {
                        hotbar[h]   = backpack[b]; // pindahkan ke hotbar
                        backpack[b] = null;        // kosongkan slot backpack
                        compactArray(backpack, BACKPACK_SIZE); // rapikan backpack
                        break;
                    }
                }
            }
        }
    }
    // ── Akses slot ────────────────────────────────────────
    public Item getActiveItem()        { return hotbar[activeIndex]; }
    public Item getItem(int i)         { return (i >= 0 && i < HOTBAR_SIZE) ? hotbar[i] : null; }
    public void toggleBackpack()    { backpackOpen = !backpackOpen; }
    public boolean isBackpackOpen() { return backpackOpen; }
    public Item getBackpackItem(int i) {
        return (i >= 0 && i < BACKPACK_SIZE) ? backpack[i] : null;
    }

    // ── Navigasi ──────────────────────────────────────────
    public void setActiveIndex(int i)  { if (i >= 0 && i < HOTBAR_SIZE) activeIndex = i; }
    public void scrollNext()           { activeIndex = (activeIndex + 1) % HOTBAR_SIZE; }
    public void scrollPrev()           { activeIndex = (activeIndex - 1 + HOTBAR_SIZE) % HOTBAR_SIZE; }
    public int  getActiveIndex()       { return activeIndex; }
    public int getBackpackActiveRow() { return backpackActiveRow; }
    public int getBackpackActiveCol() { return backpackActiveCol; }
}
