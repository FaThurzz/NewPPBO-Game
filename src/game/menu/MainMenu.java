package game.menu;

import game.engine.GameStateManager;
import game.save.SaveManager;

/**
 * Logika menu utama: navigasi pilihan dan eksekusi aksi.
 * Konsep OOP: Encapsulation
 */
public class MainMenu {

    // Pilihan menu
    public enum MenuOption { NEW_GAME, CONTINUE, QUIT }

    private final MenuOption[] options;
    private int selectedIndex = 0;

    // Apakah ada save file? → Continue aktif atau tidak
    private final boolean hasSave;

    public MainMenu() {
        hasSave = SaveManager.hasSaveFile();
        options = MenuOption.values(); // [NEW_GAME, CONTINUE, QUIT]
    }

    /** Navigasi ke pilihan di atas */
    public void navigateUp() {
        selectedIndex = (selectedIndex - 1 + options.length) % options.length;
        // Skip CONTINUE jika tidak ada save file
        if (getSelected() == MenuOption.CONTINUE && !hasSave) {
            navigateUp();
        }
    }

    /** Navigasi ke pilihan di bawah */
    public void navigateDown() {
        selectedIndex = (selectedIndex + 1) % options.length;
        // Skip CONTINUE jika tidak ada save file
        if (getSelected() == MenuOption.CONTINUE && !hasSave) {
            navigateDown();
        }
    }

    /**
     * Eksekusi pilihan yang sedang dipilih.
     * Return state game berikutnya.
     */
    public GameStateManager.State confirm() {
        return switch (getSelected()) {
            case NEW_GAME  -> {
                SaveManager.deleteSave(); // hapus save lama jika ada
                yield GameStateManager.State.PLAYING;
            }
            case CONTINUE  -> hasSave
                    ? GameStateManager.State.PLAYING
                    : GameStateManager.State.MAIN_MENU; // tidak bisa jika tidak ada save
            case QUIT      -> {
                System.exit(0);
                yield GameStateManager.State.MAIN_MENU; // tidak pernah tercapai
            }
        };
    }

    public MenuOption getSelected()  { return options[selectedIndex]; }
    public int getSelectedIndex()    { return selectedIndex; }
    public boolean hasSave()         { return hasSave; }
    public MenuOption[] getOptions() { return options; }
}