package chess;
import java.util.ArrayList;

public abstract class Piece {
    protected Position pos;
    protected boolean hasMoved;
    protected Pieces type;
    protected boolean isBlack;
    protected ArrayList<Move> availableMoves;

    protected Board chessBoard;
    protected ArrayList<Piece> ownPieces;
    protected ArrayList<Piece> oppPieces;

    public Piece(Position pos, Pieces type) {
        this.pos = pos;
        this.type = type;
        hasMoved = false;
        isBlack = Board.onBlackSide(pos);
    }
    public ArrayList<Move> getAvailableMoves() {
        return availableMoves;
    }
    public abstract void setAvailableMoves();

    public String toString() {
        return type + "_" + (isBlack ? "black" : "white");
    }
}