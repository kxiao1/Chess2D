package chess;
import java.util.ArrayList;

public abstract class Piece {
    protected Position pos;
    protected boolean hasMoved;
    protected Pieces type;
    protected boolean isBlack;

    public Piece(Position pos, Pieces type) {
        this.pos = pos;
        this.type = type;
        hasMoved = false;
        isBlack = Board.onBlackSide(pos);
    }
    public abstract ArrayList<Move> getAvailableMoves();

    public String toString() {
        return type + "_" + (isBlack ? "black" : "white");
    }
}