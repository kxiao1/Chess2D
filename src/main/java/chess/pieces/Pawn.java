package chess.pieces;
import chess.*;
import java.util.ArrayList;

public class Pawn extends Piece {
    
    public Pawn(Position pos, Pieces type) {
        super(pos, type);
    }

    public ArrayList<Move> getAvailableMoves() {
        return null;
    }
}

