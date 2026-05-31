package game.entity;

import game.engine.GamePanel;
import game.engine.KeyHandler;
import game.items.Inventory;
import game.items.Item;
import game.items.Seed;
import game.items.Tool;
import game.save.SaveData;
import game.save.SaveManager;
import game.world.TileMap;
import game.world.TilePos;
import game.engine.ImageLoader;
import java.awt.image.BufferedImage;


import java.awt.*;

/**
 * CLASS: Player
 * Konsep OOP:
 * 1. Inheritance  — extends Entity
 * 2. Polymorphism — override update() dan render()
 * 3. Encapsulation — stats private dengan getter/setter
 * Merge:
 * - Stamina system
 * - Inventory + item usage
 * - getFacingTile() untuk interaksi tile (cangkul, tanam, siram, panen)
 * - Collision dengan batas map
 * - Getter/setter dari versi simplified
 */
public class Player extends Entity {

    // ── Stats (private = Encapsulation) ───────────────────
    private int hp, maxHp;
    private int stamina, maxStamina;
    private int level, exp, money;

    // ── Inventory ─────────────────────────────────────────
    private final Inventory inventory = new Inventory();

    // ── Input & map ───────────────────────────────────────
    private final KeyHandler key;
    private TileMap    tileMap;

    // ── Animasi ───────────────────────────────────────────
    private String  direction = "down";
    private int     animTimer = 0;
    private int     animFrame = 0;
    private boolean moving    = false;

    private BufferedImage front1, front2, back1, back2, left1, left2, right1, right2;

    public Player(KeyHandler key, TileMap tileMap) {
        super(
            GamePanel.TILE_SCALED * 5,  // x awal
            GamePanel.TILE_SCALED * 5,  // y awal
            GamePanel.TILE_SCALED,       // width
            GamePanel.TILE_SCALED,       // height
            4                            // speed
        );
        this.key     = key;
        this.tileMap = tileMap;

        // Inisialisasi stats
        this.hp          = 100; this.maxHp      = 100;
        this.stamina     = 100; this.maxStamina = 100;
        this.level       = 1;   this.exp        = 0;
        this.money       = 500;

        // Texture
        back1  = ImageLoader.load("resources/character/player/back.png");
        back2  = ImageLoader.load("resources/character/player/back2.png");
        front1 = ImageLoader.load("resources/character/player/front.png");
        front2 = ImageLoader.load("resources/character/player/front1.png");
        left1  = ImageLoader.load("resources/character/player/left.png");
        left2  = ImageLoader.load("resources/character/player/left2.png");
        right1 = ImageLoader.load("resources/character/player/right.png");
        right2 = ImageLoader.load("resources/character/player/right1.png");

        sprite = front1;

        // Item awal di inventory
        inventory.addItem(Tool.Hoe());
        inventory.addItem(Tool.WateringCan());
        inventory.addItem(Tool.Scythe());
        inventory.addItem(Tool.Pickaxe());
        inventory.addItem(Tool.Axe());
        inventory.addItem(Seed.stroberi());
        inventory.addItem(Seed.carrot());
        inventory.addItem(Seed.potato());
    }

    // ── Update (Polymorphism: override dari Entity) ────────
    @Override
    public void update() {
        key.tick();

        if (inventory.isBackpackOpen()) {
            // ── Backpack TERBUKA → arrow untuk navigasi baris ──
            // Arrow up/down TIDAK menggerakkan player
            if (key.isUpJust())   inventory.backpackRowUp();
            if (key.isDownJust()) inventory.backpackRowDown();

            // 1-9 → pilih kolom
            for (int i = 0; i < 9; i++) {
                if (key.isSlot(i)) inventory.setBackpackActiveCol(i);
            }

            // Z → pakai item backpack
            if (key.isActionJustPressed()) {
                int  idx    = inventory.getBackpackActiveIndex();
                Item active = inventory.getBackpackActiveItem();
                if (active != null && active.isUsable()) {
                    active.use(this, tileMap);
                    if (active.getQuantity() <= 0) {
                        inventory.removeBackpackAt(idx);
                    }
                }
            }

            // F → jual item backpack
            if (key.isSellJustPressed()) sellBackpackItem();

        } else {
            // ── Backpack TERTUTUP → arrow untuk gerak player ──
            int dx = 0, dy = 0;
            moving = false;

            if (key.isUp())    { dy = -getSpeed(); direction = "up";    moving = true; }
            if (key.isDown())  { dy =  getSpeed(); direction = "down";  moving = true; }
            if (key.isLeft())  { dx = -getSpeed(); direction = "left";  moving = true; }
            if (key.isRight()) { dx =  getSpeed(); direction = "right"; moving = true; }

            if (dx != 0) moveX(dx);
            if (dy != 0) moveY(dy);

            // Animasi
            if (moving) {
                if (direction.equals("up")) {
                    sprite = (animFrame % 2 == 0) ? back1 : back2;
                } else if (direction.equals("down")) {
                    sprite = (animFrame % 2 == 0) ? front1 : front2;
                } else if (direction.equals("left")) {
                    sprite = (animFrame % 2 == 0) ? left1 : left2;
                } else if (direction.equals("right")) {
                    sprite = (animFrame % 2 == 0) ? right1 : right2;
                }
            }

            // 1-9 → pilih slot hotbar
            for (int i = 0; i < 9; i++) {
                if (key.isSlot(i)) inventory.setActiveIndex(i);
            }

            // Z → pakai item hotbar
            if (key.isActionJustPressed()) {
                int  idx    = inventory.getActiveIndex();
                Item active = inventory.getActiveItem();
                if (active != null && active.isUsable()) {
                    active.use(this, tileMap);
                    if (active.getQuantity() <= 0) {
                        inventory.removeAt(idx);
                    }
                }
            }

            // F → jual item hotbar
            if (key.isSellJustPressed()) sellActiveItem();
        }

        // I → buka/tutup backpack (selalu aktif)
        if (key.isInventoryJustPressed()) {
            inventory.toggleBackpack();
        }
    }

    /** Gerak horizontal dengan collision tile + batas map */
    private void moveX(int dx) {
        int ts   = GamePanel.TILE_SCALED;
        int mapW = tileMap.getCols() * ts;
        int newX = Math.max(0, Math.min(getX() + dx, mapW - getWidth()));

        int col   = (dx > 0) ? (newX + getWidth() - 1) / ts : newX / ts;
        int rowTop = getY() / ts;
        int rowBot = (getY() + getHeight() - 1) / ts;

        if (tileMap.isPassable(col, rowTop) && tileMap.isPassable(col, rowBot)) {
            setX(newX);
        }
    }

    /** Gerak vertikal dengan collision tile + batas map */
    private void moveY(int dy) {
        int ts   = GamePanel.TILE_SCALED;
        int mapH = tileMap.getRows() * ts;
        int newY = Math.max(0, Math.min(getY() + dy, mapH - getHeight()));

        int row    = (dy > 0) ? (newY + getHeight() - 1) / ts : newY / ts;
        int colLeft  = getX() / ts;
        int colRight = (getX() + getWidth() - 1) / ts;

        if (tileMap.isPassable(colLeft, row) && tileMap.isPassable(colRight, row)) {
            setY(newY);
        }
    }

    // ── Render (Polymorphism: override dari Entity) ────────
    @Override
    public void render(Graphics2D g, int camX, int camY) {
        int screenX = getX() - camX;
        int screenY = getY() - camY;
        int size    = GamePanel.TILE_SCALED;

        if (sprite != null) {
            g.drawImage(sprite, screenX, screenY, size, size, null);
        } else {
            // Placeholder pixel-art player
            g.setColor(new Color(70, 130, 180));
            g.fillRect(screenX + 8, screenY + 4, size - 16, size - 8);  // badan
            g.setColor(new Color(255, 210, 160));
            g.fillRect(screenX + 12, screenY, size - 24, size / 2);     // kepala

            // Indikator arah
            g.setColor(Color.RED);
            switch (direction) {
                case "up"    -> g.fillRect(screenX + size/2 - 3, screenY,          6, 6);
                case "down"  -> g.fillRect(screenX + size/2 - 3, screenY + size-6, 6, 6);
                case "left"  -> g.fillRect(screenX,               screenY + size/2, 6, 6);
                case "right" -> g.fillRect(screenX + size - 6,    screenY + size/2, 6, 6);
            }

            // Label debug
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Courier New", Font.PLAIN, 8));
            g.drawString(direction.charAt(0) + "" + animFrame, screenX + 2, screenY - 2);
        }
    }

    /**
     * Kembalikan posisi tile yang sedang "dihadapi" player.
     * Dipakai oleh Tool dan Seed saat use() dipanggil.
     */
    public TilePos getFacingTile() {
        int ts  = GamePanel.TILE_SCALED;
        int col = (getX() + getWidth()  / 2) / ts;
        int row = (getY() + getHeight() / 2) / ts;
        return switch (direction) {
            case "up"    -> new TilePos(col, row - 1);
            case "down"  -> new TilePos(col, row + 1);
            case "left"  -> new TilePos(col - 1, row);
            case "right" -> new TilePos(col + 1, row);
            default      -> new TilePos(col, row);
        };
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    // ── Stat methods ──────────────────────────────────────

    public void gainExp(int amount) {
        exp += amount;
        if (exp >= level * 100) levelUp();
    }

    private void levelUp() {
        exp = 0; level++; maxHp += 10; hp = maxHp;
        System.out.println("Level Up! Sekarang level " + level);
    }

    public void takeDamage(int dmg) {
        hp = Math.max(0, hp - dmg);
        if (hp == 0) System.out.println("Player mati!");
    }

    /**
     * Jual seluruh item di slot aktif.
     * Cara kerja:
     * 1. Ambil item yang sedang aktif di hotbar
     * 2. Cek apakah item bisa dijual (sellPrice > 0)
     * 3. Hitung total uang = harga × jumlah item
     * 4. Tambahkan uang ke player
     * 5. Hapus item dari inventory
     * Tool tidak bisa dijual karena sellPrice-nya 0.
     */
    private void sellActiveItem() {
        Inventory inv    = getInventory();
        Item      active = inv.getActiveItem();
        // Tidak ada item di slot aktif
        if (active == null) {
            System.out.println("Tidak ada item untuk dijual.");
            return;
        }
        // Tool tidak dijual (sellPrice = 0 di constructor Tool)
        if (active.getSellPrice() <= 0) {
            System.out.println(active.getName() + " tidak bisa dijual.");
            return;
        }
        // Hitung total uang dari semua item di slot ini
        // Contoh: 5 Parsnip × $35 = $175
        int total = active.getSellPrice() * active.getQuantity();
        // Tambah uang ke player
        addMoney(total);
        System.out.println("Jual " + active.getQuantity() + "x "
                + active.getName() + " → $" + total
                + " (total uang: $" + getMoney() + ")");
        // Hapus seluruh item di slot aktif
        inv.removeAt(inv.getActiveIndex());
    }
    private void sellBackpackItem() {
        int  idx    = inventory.getBackpackActiveIndex();
        Item active = inventory.getBackpackActiveItem();

        if (active == null) {
            System.out.println("Tidak ada item di slot ini.");
            return;
        }
        if (active.getSellPrice() <= 0) {
            System.out.println(active.getName() + " tidak bisa dijual.");
            return;
        }

        int total = active.getSellPrice() * active.getQuantity();
        addMoney(total);
        System.out.println("Jual " + active.getQuantity() + "x "
                + active.getName() + " → $" + total);
        inventory.removeBackpackAt(idx);
    }

    public void addMoney(int amount) {  // Math.max(0,...) mencegah uang jadi negatif
        money = Math.max(0, money + amount); }

    public void loadFromSave(SaveData data) {
        setX(data.playerX);
        setY(data.playerY);
        this.hp         = data.hp;
        this.maxHp      = data.maxHp;
        this.stamina    = data.stamina;
        this.maxStamina = data.maxStamina;
        this.level      = data.level;
        this.exp        = data.exp;
        this.money      = data.money;
        if (data.hotbarData != null && !data.hotbarData.isEmpty()) {
            inventory.clearAll(); // clear HANYA jika ada data valid yang mau di-load
            String[] slots = data.hotbarData.split(";", -1);
            for (int i = 0; i < slots.length && i < Inventory.HOTBAR_SIZE; i++) {
                Item item = SaveManager.deserializeItem(slots[i]);
                if (item != null) inventory.setHotbarSlot(i, item);
            }
        }

        if (data.backpackData != null && !data.backpackData.isEmpty()) {
            String[] slots = data.backpackData.split(";", -1);
            for (int i = 0; i < slots.length && i < Inventory.BACKPACK_SIZE; i++) {
                Item item = SaveManager.deserializeItem(slots[i]);
                if (item != null) inventory.setBackpackSlot(i, item);
            }
        }
    }

    // ── Getters (Encapsulation) ────────────────────────────
    public int       getHp()         { return hp; }
    public int       getMaxHp()      { return maxHp; }
    public int       getStamina()    { return stamina; }
    public int       getMaxStamina() { return maxStamina; }
    public int       getLevel()      { return level; }
    public int       getExp()        { return exp; }
    public int       getMoney()      { return money; }
    public Inventory getInventory()  { return inventory; }

    // Setter stamina (dipakai oleh Tool saat use)
    public void setStamina(int s)    { this.stamina = Math.max(0, Math.min(s, maxStamina)); }
    public void setHp(int h)         { this.hp      = Math.max(0, Math.min(h, maxHp)); }
}
