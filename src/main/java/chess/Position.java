package chess;
public class Position {
    private int x;
    private int y;
    private String name;
    
    Piece piece;
    boolean isLastRow;
    boolean isBlack;

    Position(int x, int y, Piece piece) {
        assert(Board.isValidPos(x,y));
        this.x = x;
        this.y = y;
        this.name = String.valueOf((char)(y + 65)) + (x + 1);
        this.piece = piece;
        
        isLastRow = (y == 0 || y == (Board.NumY-1));
        isBlack = ((this.x % 2) ^ (this.y % 2)) == 0;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return name;
    }
}