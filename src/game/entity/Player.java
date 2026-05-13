package game.entity;

import game.engine.GamePanel;
import game.engine.KeyHandler;
import game.items.Inventory;
import game.items.Item;
import game.items.Seed;
import game.items.Tool;
import game.world.TileMap;
import game.world.TilePos;

import java.awt.*;

/**
 * CLASS: Player
 *
 * Konsep OOP:
 * 1. Inheritance  — extends Entity
 * 2. Polymorphism — override update() dan render()
 * 3. Encapsulation — stats private dengan getter/setter
 *
 * Merge:
 * - Stamina system dari versi temanmu
 * - Inventory + item usage dari versi temanmu
 * - getFacingTile() untuk interaksi tile (cangkul, tanam, siram, panen)
 * - Collision dengan batas map dari versi temanmu
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
    private final TileMap    tileMap;

    // ── Animasi ───────────────────────────────────────────
    private String  direction = "down";
    private int     animTimer = 0;
    private int     animFrame = 0;
    private boolean moving    = false;

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

        // Item awal di inventory
        inventory.addItem(Tool.basicHoe());
        inventory.addItem(Tool.basicWateringCan());
        inventory.addItem(Tool.basicScythe());
        inventory.addItem(Seed.parsnip());
    }

    // ── Update (Polymorphism: override dari Entity) ────────
    @Override
    public void update() {
        int dx = 0, dy = 0;
        moving = false;

        if (key.isUp())    { dy = -getSpeed(); direction = "up";    moving = true; }
        if (key.isDown())  { dy =  getSpeed(); direction = "down";  moving = true; }
        if (key.isLeft())  { dx = -getSpeed(); direction = "left";  moving = true; }
        if (key.isRight()) { dx =  getSpeed(); direction = "right"; moving = true; }

        if (dx != 0) moveX(dx);
        if (dy != 0) moveY(dy);

        // Animasi berjalan
        if (moving) {
            animTimer++;
            if (animTimer >= 10) { animTimer = 0; animFrame = (animFrame + 1) % 4; }
        } else {
            animFrame = 0;
        }

        // Pilih slot inventory dengan tombol 1-9
        for (int i = 0; i < 9; i++) {
            if (key.isSlot(i)) inventory.setActiveIndex(i);
        }

        // Gunakan item aktif dengan Z / Enter
        key.tick(); // hitung actionJustPressed
        if (key.isActionJustPressed()) {
            Item active = inventory.getActiveItem();
            if (active != null && active.isUsable()) {
                active.use(this, tileMap);
            }
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

    public void addMoney(int amount) { money += amount; }

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
