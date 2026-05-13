package game.world;

/**
 * FARM TILE — data pertanian satu tile (dicangkul? ada tanaman? sudah disiram?)
 * Konsep OOP: Encapsulation (field private, diakses lewat method)
 */
public class FarmTile {

    private boolean tilled    = false;
    private boolean watered   = false;
    private boolean hasPlant  = false;
    private String  cropType  = null;
    private int     growStage = 0;   // 0-3, panen saat >=3

    // ── Aksi ──────────────────────────────────────────────

    /** Cangkul tile ini */
    public void till()  { tilled = true; }

    /** Siram tanaman */
    public void water() { watered = true; }

    /** Tanam benih */
    public void plant(String crop) {
        this.hasPlant = true;
        this.cropType = crop;
        this.growStage = 0;
    }

    /** Proses satu hari: kalau disiram, tambah grow stage */
    public void advanceDay() {
        if (hasPlant && watered) growStage++;
        watered = false; // reset siram tiap hari
    }

    /** Apakah sudah siap panen? */
    public boolean isHarvestable() { return hasPlant && growStage >= 3; }

    /** Reset tile setelah panen */
    public void reset() {
        hasPlant  = false;
        cropType  = null;
        growStage = 0;
        watered   = false;
    }

    // ── Getters ───────────────────────────────────────────
    public boolean isTilled()   { return tilled; }
    public boolean isWatered()  { return watered; }
    public boolean isHasPlant() { return hasPlant; }
    public String  getCropType(){ return cropType; }
    public int     getGrowStage(){ return growStage; }
}
