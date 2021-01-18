package chess.pieces;
import chess.*;
import java.util.ArrayList;

public class King extends Piece {
    private int disp[] = {-1, 0, 1};

    public King(Position pos, Pieces type) {
        super(pos, type);
    }
    public void setAvailableMoves() {
        for (int x:disp) {
            for (int y:disp) {
                int newX = pos.getX() + x;
                int newY = pos.getY() + y;
                
                // if out of bounds, rej

                // if blocked, rej

                // if under check, rej
            }
        }
    }
}