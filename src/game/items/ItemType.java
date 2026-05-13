package game.items;

/**
 * ENUM: ItemType
 * Konsep OOP: Enum
 * Mendefinisikan semua jenis item yang ada di game.
 */
public enum ItemType {
    SEED,       // benih yang bisa ditanam
    CROP,       // hasil panen
    TOOL,       // alat (cangkul, kaleng air, dll)
    FOOD,       // makanan yang restore stamina/hp
    MATERIAL,   // bahan baku (kayu, batu, ore)
    FISH,       // hasil memancing
    ARTIFACT,   // barang antik / collectible
    GIFT        // item untuk diberikan ke NPC
}
