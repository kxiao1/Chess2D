package chess.pieces;

import chess.*;
import java.util.ArrayList;

public class Bishop extends Piece {
    private int disp[] = { -1, 1 };

    public Bishop(Position pos, Pieces type) {
        super(pos, type);
    }

    public void setAvailableMoves() {
        var temp = new ArrayList<Move>();
        for (int x : disp) {
            for (int y : disp) {
                var mult = 1;
                var canReach = !(chessBoard.cannotReachDestination(pos, x * mult, y * mult));
                while (canReach) { // short-circuits
                    if (!chessBoard.willBeChecked(pos, x * mult, y * mult)) {
                        var m = chessBoard.createMove(toString(), pos, x * mult, y * mult, Action.NONE);
                        temp.add(m);
                    }
                    if (chessBoard.willHitOpponentPiece(pos, x * mult, y * mult)) {
                        break; // cannot 'jump over' opponent piece without capturing
                    }
                    ++mult;
                    canReach = !(chessBoard.cannotReachDestination(pos, x * mult, y * mult));
                }
            }
        }
        availableMoves = temp;
    }

    public ArrayList<Position> getAttackPath(Position KingPos) {
        var kingX = KingPos.getX();
        var kingY = KingPos.getY();
        var xdist = kingX - pos.getX();
        var ydist = kingY - pos.getY();
        var absX = Math.abs(xdist);
        var absY = Math.abs(ydist);
        boolean xneg = xdist < 0;
        boolean yneg = ydist < 0;

        if (absX == absY) { // clearly both will be > 0 in this case
            // attacking diagonally
            var temp = new ArrayList<Position>();
            for (int dist = absY; dist > 1; --dist) {
                var newXDist = xneg ? -dist + 1 : dist - 1;
                var newYDist = yneg ? -dist + 1 : dist - 1;
                var newSq = new Position(kingX - newXDist, kingY - newYDist, null);
                temp.add(newSq);
            }
            return temp;
        }
        return null;
    }
}