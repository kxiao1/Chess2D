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

    protected Piece(Position pos, Pieces type) {
        if (pos == null) {
            throw new NullPointerException("The starting position must be specified.");
        } 
        if (type == Pieces.NONE) {
            throw new IllegalArgumentException("The type of the piece must be specified.");
        }
        this.pos = pos;
        this.type = type;
        hasMoved = false;
        isBlack = Board.onBlackSide(pos);
    }

    ArrayList<Move> getAvailableMoves() {
        return availableMoves;
    }

    protected abstract void setAvailableMoves();

    // Returns the squares on the path from this piece to the King
    // null if not attacking, empty array if not blocking
    protected abstract ArrayList<Position> getAttackPath(Position KingPos);

    public String toString() {
        return type + "_" + (isBlack ? "black" : "white");
    }
}