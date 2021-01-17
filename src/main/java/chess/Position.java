package chess;
public class Position {
    private int x;
    private int y;
    private String name;
    private Piece piece;

    boolean isBlack;

    Position(int x, int y, Piece piece) {
        assert(Board.isValidPos(x,y));
        this.x = x;
        this.y = y;
        this.name = String.valueOf((char)(y + 65)) + (x + 1);
        this.piece = piece;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public String toString() {
        return name;
    }
}