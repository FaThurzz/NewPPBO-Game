package game.engine;

/**
 * Mengelola state game saat ini.
 * Konsep OOP: Encapsulation + Enum
 */
public class GameStateManager {

    public enum State {
        MAIN_MENU,   // tampilan menu utama
        PLAYING,     // sedang bermain
    }

    private State current = State.MAIN_MENU; // mulai dari menu

    public void setState(State s) { current = s; }
    public boolean is(State s)    { return current == s; }
}