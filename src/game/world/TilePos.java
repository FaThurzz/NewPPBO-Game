package game.world;

/**
 * TILE POS — posisi sebuah tile di peta (kolom, baris)
 * Konsep OOP: Encapsulation
 */
public class TilePos {
    private final int col, row;

    public TilePos(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() { return col; }
    public int getRow() { return row; }
}
