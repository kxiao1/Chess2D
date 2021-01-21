package chess.pieces;

import chess.*;
import java.util.ArrayList;

public class Knight extends Piece {
    private int xdisp[] = { -2, -1, 1, 2 };

    public Knight(Position pos, Pieces type) {
        super(pos, type);
    }

    public void setAvailableMoves() {
        var temp = new ArrayList<Move>();
        for (int x : xdisp) {
            int ydisp[] = Math.abs(x) == 2 ? new int[] { -1, 1 } : new int[] { -2, 2 };
            for (int y : ydisp) {
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
        for (int x : xdisp) {
            int ydisp[] = Math.abs(x) == 2 ? new int[] { -1, 1 } : new int[] { -2, 2 };
            for (int y : ydisp) {
                var newX = pos.getX() + x;
                var newY = pos.getY() + y;
                if (newX == KingPos.getX() && newY == KingPos.getY()) {
                    return new ArrayList<Position>(); // any attack is unblocked
                }
            }
        }
        return null; // not attacking
    }
}
