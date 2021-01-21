package chess.pieces;
import chess.*;
import java.util.ArrayList;

public class King extends Piece {
    private int disp[] = {-1, 0, 1};

    public King(Position pos, Pieces type) {
        super(pos, type);
    }
    public void setAvailableMoves() {

        var temp = new ArrayList<Move>();
        for (int x:disp) {
            for (int y:disp) {
                if (x == 0 && y == 0) {
                    continue; // this case is equivalent to not moving
                }
                var ok = !(chessBoard.violatesBasicRules(pos, x, y));
                if (ok) {
                    var m = chessBoard.createMove(toString(), pos, x, y, Action.NONE);
                    temp.add(m);
                }
            }
        }

        availableMoves = temp;
    }

    public ArrayList<Position> getAttackPath(Position KingPos) {
        return null; // a King will never find itself attacking another King
    }
}