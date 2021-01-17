package chess.pieces;
import chess.*;
import java.util.ArrayList;

public class King extends Piece {
    private int disp[] = {-1, 0, 1};

    public King(Position pos, Pieces type) {
        super(pos, type);
    }
    public ArrayList<Move> getAvailableMoves() {
        var moves = new ArrayList<Move>();
        for (int x:disp) {
            for (int y:disp) {
                int newX = pos.getX() + x;
                int newY = pos.getY() + y;
                
                // if out of bounds, rej

                // if under check, rej
            }
        }
        return moves;
    }
}