package game.world;

/**
 * TIME SYSTEM — sistem waktu in-game
 * Konsep OOP: Encapsulation
 * Semua field private, hanya bisa diakses lewat method.
 * Cara kerja:
 *   60 frame  = 1 menit in-game
 *   6 menit   = 1 jam in-game
 *   16 jam    = 1 hari in-game (jam 6 pagi - jam 10 malam)
 *   28 hari   = 1 musim
 *   4 musim   = 1 tahun
 */
public class TimeSystem {

    // ── Waktu ──────────────────────────────────────────────
    private int hour   = 6;  // mulai jam 6 pagi
    private int minute = 0;
    private int day    = 1;
    private Season season = Season.SPRING;
    // ── Ticker ─────────────────────────────────────────────
    /**
     * tickCounter dihitung setiap frame (60x per detik).
     * Saat mencapai TICKS_PER_MINUTE, 10 menit in-game berlalu.
     * Kenapa 60? Karena game berjalan di 60 FPS.
     * Jadi 60 tick = 1 detik real = 10 menit in-game.
     * Artinya 1 hari in-game (960 menit) = 96 detik real ≈ 1.5 menit.
     */
    private int tickCounter = 0;
    private static final int TICKS_PER_MINUTE = 60;
    // ── Referensi ke TileMap ───────────────────────────────
    /**
     * TimeSystem perlu akses ke TileMap agar bisa
     * memanggil advanceAllFarmTiles() saat hari berganti.
     * Ini adalah contoh dependency injection via constructor.
     */
    private final TileMap tileMap;
    // ── Listener untuk event pergantian hari ──────────────
    /**
     * DayChangeListener adalah interface sederhana.
     * Dipakai agar GamePanel atau Player bisa "mendengarkan"
     * saat hari berganti (misalnya untuk restore stamina).
     * Ini contoh penggunaan interface sebagai callback.
     */
    // Interface ini ditulis DI DALAM class TimeSystem yang disebut "nested interface"
    public interface DayChangeListener {
        void onDayChanged(int newDay, Season season);
    }
    // TimeSystem menyimpan REFERENSI ke listener
    // bukan mengimplementasikannya
    private DayChangeListener dayChangeListener = null;

    // ── Constructor ────────────────────────────────────────
    public TimeSystem(TileMap tileMap) {
        this.tileMap = tileMap;
    }
    // ── Update (dipanggil tiap frame dari GamePanel) ───────
    /**
     * Dipanggil 60x per detik dari GamePanel.update().
     * Setiap kali tickCounter mencapai TICKS_PER_MINUTE,
     * waktu in-game maju 10 menit.
     */
    public void update() {
        tickCounter++;
        if (tickCounter >= TICKS_PER_MINUTE) {
            tickCounter = 0;
            advanceMinute();
        }
    }
    // ── Maju waktu ─────────────────────────────────────────
    /**
     * Tambah 10 menit ke waktu in-game.
     * Jika menit sudah >= 60, maju ke jam berikutnya.
     * Jika jam sudah >= 22 (jam 10 malam), akhiri hari.
     */
    private void advanceMinute() {
        minute += 10;
        if (minute >= 60) {
            minute = 0;
            hour++;
        }
        if (hour >= 22) {
            endDay();
        }
    }
    /**
     * Dijalankan saat jam menunjukkan 22:00 (10 malam).
     * Urutan kejadian:
     * 1. Reset jam ke pagi (jam 6)
     * 2. Tambah hari
     * 3. Semua FarmTile diproses (tanaman tumbuh)
     * 4. Cek pergantian musim
     * 5. Panggil listener (untuk restore stamina, dll)
     */
    private void endDay() {
        hour   = 6;
        minute = 0;
        day++;

        // Semua tile pertanian diproses:
        // tanaman yang disiram hari ini akan tumbuh (growStage++)
        tileMap.advanceAllFarmTiles();

        // Cek pergantian musim setiap 28 hari
        if (day > 28) {
            day    = 1;
            season = season.next(); // SPRING → SUMMER → FALL → WINTER → SPRING
            System.out.println("Musim berganti: " + season.getDisplayName());
        }

        System.out.println("Hari baru: Day " + day + " | " + season.getDisplayName());

        // Beritahu listener (misalnya GamePanel untuk restore stamina)
        if (dayChangeListener != null) {
            dayChangeListener.onDayChanged(day, season);
        }
    }
    // ── Setter listener ────────────────────────────────────
    public void setDayChangeListener(DayChangeListener listener) {
        this.dayChangeListener = listener;
    }
    // ── Getters ────────────────────────────────────────────
    /**
     * Format waktu untuk ditampilkan di HUD.
     * %02d artinya tampilkan minimal 2 digit, isi 0 jika kurang.
     * Contoh: jam 8, menit 5 → "08:05"
     */
    public String getTimeString() {
        return String.format("%02d:%02d", hour, minute);
    }
    public int    getDay()      { return day; }
    public int    getHour()     { return hour; }
    public Season getSeason()   { return season; }
    /**
     * Kembalikan persentase hari yang sudah berlalu (0.0 - 1.0).
     * Berguna untuk menggambar progress bar waktu di HUD.
     * Jam 6 pagi = 0.0, jam 10 malam = 1.0
     */
    public float getDayProgress() {
        int totalMinutes  = (hour - 6) * 60 + minute; // menit sejak jam 6
        int maxMinutes    = (22  - 6) * 60;            // total menit dalam 1 hari
        return (float) totalMinutes / maxMinutes;
    }
}