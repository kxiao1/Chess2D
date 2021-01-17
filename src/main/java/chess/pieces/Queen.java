package chess.pieces;
import chess.*;
import java.util.ArrayList;

public class Queen extends Piece {
    
    public Queen(Position pos, Pieces type) {
        super(pos, type);
    }

    public ArrayList<Move> getAvailableMoves() {
        return null;
    }
}
