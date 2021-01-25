package chess;
public class Position {
    private int x;
    private int y;
    private String name;
    
    Piece piece;
    boolean isLastRow;
    boolean isBlack;

    static int getXFromString(String str) {
        var chr = str.charAt(0);
        return (int)chr - 97;
    }
    static int getYFromString(String str) {
        var chr = str.charAt(1);
        return Character.getNumericValue(chr);
    }

    public static boolean samePos(Position pos1, Position pos2) {
        if (pos1 == null || pos2 == null) {
            var errStr = (pos1 == null ? "Pos1" : "Pos2") + " is null.";
            throw new NullPointerException(errStr);
        }
        return (pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY());
    }

    public Position(int x, int y, Piece piece) {
        if (!Board.isValidPos(x, y)) {
            var errStr = "The position (" + x + "," + y + ") is invalid.";
            throw new ArrayIndexOutOfBoundsException(errStr);
        }
        this.x = x;
        this.y = y;
        this.name = String.valueOf((char)(x + 97)) + (y + 1);
        this.piece = piece;
        
        isLastRow = Board.isLastRow(x, y);
        isBlack = Board.isBlack(x, y);
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