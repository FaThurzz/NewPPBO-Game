package game.world;

/**
 * SEASON — musim dalam game
 * Konsep OOP: Enum dengan method
 */
public enum Season {
    SPRING, SUMMER, FALL, WINTER;

    public String getDisplayName() {
        return switch (this) {
            case SPRING -> "Spring"; case SUMMER -> "Summer";
            case FALL   -> "Fall";   case WINTER -> "Winter";
        };
    }

    /** Apakah musim ini bisa bertani? */
    public boolean canFarm() { return this != WINTER; }

    /** Musim berikutnya (WINTER → SPRING) */
    public Season next() { return values()[(this.ordinal() + 1) % 4]; }
}
