package game.store;
import game.items.Item;

/**
 * Merepresentasikan satu produk di katalog toko.
 * Berbeda dari Item biasa karena menyimpan harga beli
 * dan stok yang tersedia di toko.
 */
public class StoreItem {

    private final Item   item;      // item yang dijual
    private final int    price;     // harga beli dari toko
    private final String category;  // "Seeds", "Tools", "Food"

    public StoreItem(Item item, int price, String category) {
        this.item     = item;
        this.price    = price;
        this.category = category;
    }

    public Item   getItem()     { return item; }
    public int    getPrice()    { return price; }
    public String getCategory() { return category; }
}