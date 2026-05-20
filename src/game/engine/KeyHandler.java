package game.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * KEY HANDLER
 * Konsep OOP: Encapsulation
 *
 * Mendukung gerakan (WASD/Arrow), aksi (Z/Enter), jual (F),
 * dan slot inventory (1-9). Fitur "just pressed" mencegah aksi
 * terpicu berkali-kali selama tombol ditahan.
 */
public class KeyHandler extends KeyAdapter {

    // ── Gerakan ────────────────────────────────────────────
    private boolean up, down, left, right;

    // ── Aksi utama (Z / Enter) ─────────────────────────────
    private boolean action;
    private boolean actionWasPressed  = false;
    private boolean actionJustPressed = false;

    // ── Jual item (F) ──────────────────────────────────────
    private boolean sell;
    private boolean sellWasPressed  = false;
    private boolean sellJustPressed = false;

    // ── Slot inventory (tombol 1-9) ────────────────────────
    private final boolean[] slot = new boolean[9];

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> up     = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> down   = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> left   = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right  = true;
            case KeyEvent.VK_E, KeyEvent.VK_SPACE -> action = true;
            case KeyEvent.VK_F                    -> sell   = true;
            case KeyEvent.VK_1 -> slot[0] = true;
            case KeyEvent.VK_2 -> slot[1] = true;
            case KeyEvent.VK_3 -> slot[2] = true;
            case KeyEvent.VK_4 -> slot[3] = true;
            case KeyEvent.VK_5 -> slot[4] = true;
            case KeyEvent.VK_6 -> slot[5] = true;
            case KeyEvent.VK_7 -> slot[6] = true;
            case KeyEvent.VK_8 -> slot[7] = true;
            case KeyEvent.VK_9 -> slot[8] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP    -> up     = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN  -> down   = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT  -> left   = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right  = false;
            case KeyEvent.VK_E, KeyEvent.VK_SPACE -> action = false;
            case KeyEvent.VK_F                    -> sell   = false;
            case KeyEvent.VK_1 -> slot[0] = false;
            case KeyEvent.VK_2 -> slot[1] = false;
            case KeyEvent.VK_3 -> slot[2] = false;
            case KeyEvent.VK_4 -> slot[3] = false;
            case KeyEvent.VK_5 -> slot[4] = false;
            case KeyEvent.VK_6 -> slot[5] = false;
            case KeyEvent.VK_7 -> slot[6] = false;
            case KeyEvent.VK_8 -> slot[7] = false;
            case KeyEvent.VK_9 -> slot[8] = false;
        }
    }

    /**
     * Dipanggil tiap frame dari Player.update().
     * Menghitung "just pressed" — true hanya pada frame pertama tombol ditekan.
     */
    public void tick() {
        actionJustPressed = action && !actionWasPressed;
        actionWasPressed  = action;

        sellJustPressed = sell && !sellWasPressed;
        sellWasPressed  = sell;
    }

    // ── Getters (Encapsulation) ────────────────────────────
    public boolean isUp()                { return up; }
    public boolean isDown()              { return down; }
    public boolean isLeft()              { return left; }
    public boolean isRight()             { return right; }
    public boolean isActionJustPressed() { return actionJustPressed; }
    public boolean isSellJustPressed()   { return sellJustPressed; }
    public boolean isSlot(int i)         { return i >= 0 && i < 9 && slot[i]; }
}
