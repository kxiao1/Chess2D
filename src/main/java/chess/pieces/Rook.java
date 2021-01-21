package chess.pieces;

import chess.*;
import java.util.ArrayList;

public class Rook extends Piece {
    private int disp[] = { -1, 1 };

    public Rook(Position pos, Pieces type) {
        super(pos, type);
    }

    public void setAvailableMoves() {
        var temp = new ArrayList<Move>();

        // horizontal movements
        for (var x : disp) {
            var mult = 1;
            var canReach = !(chessBoard.cannotReachDestination(pos, x * mult, 0));
            while (canReach) { // short-circuits
                if (!chessBoard.willBeChecked(pos, x * mult, 0)) {
                    var m = chessBoard.createMove(toString(), pos, x * mult, 0, Action.NONE);
                    temp.add(m);
                }
                if (chessBoard.willHitOpponentPiece(pos, x * mult, 0)) {
                    break; // cannot 'jump over' opponent piece without capturing
                }
                ++mult;
                canReach = !(chessBoard.cannotReachDestination(pos, x * mult, 0));
            }

        }
        for (var y : disp) {
            var mult = 1;
            var canReach = !(chessBoard.cannotReachDestination(pos, 0, y * mult));
            while (canReach) { // short-circuits
                if (!chessBoard.willBeChecked(pos, 0, y * mult)) {
                    var m = chessBoard.createMove(toString(), pos, 0, y * mult, Action.NONE);
                    temp.add(m);
                }
                if (chessBoard.willHitOpponentPiece(pos, 0, y * mult)) {
                    break; // cannot 'jump over' opponent piece without capturing
                }
                ++mult;
                canReach = !(chessBoard.cannotReachDestination(pos, 0, y * mult));
            }

        }
        // TODO castle

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

        if (xdist == 0) {
            // attacking vertically
            var temp = new ArrayList<Position>();
            for (int dist = absY; dist > 1; --dist) {
                var newYDist = yneg ? -dist + 1 : dist - 1;
                var newSq = new Position(kingX, kingY - newYDist, null);
                temp.add(newSq);
            }
            return temp;
        }
        if (ydist == 0) {
            // attacking horizontally
            var temp = new ArrayList<Position>();
            for (int dist = absX; dist > 1; --dist) {
                var newXDist = xneg ? -dist + 1 : dist - 1;
                var newSq = new Position(kingX - newXDist, kingY, null);
                temp.add(newSq);
            }
            return temp;
        }

        return null;
    }

}
