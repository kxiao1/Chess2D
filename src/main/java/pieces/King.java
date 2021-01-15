package chess.src.main.java.pieces;
import chess.src.main.java.*;
import java.util.ArrayList;

class King extends Piece {
    private int disp[] = {-1, 0, 1};

    King(Position pos) {
        super(pos);
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