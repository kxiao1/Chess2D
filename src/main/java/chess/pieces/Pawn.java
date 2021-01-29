package chess.pieces;

import chess.*;
import java.util.ArrayList;

public class Pawn extends Piece {

    public Pawn(Position pos, Pieces type) {
        super(pos, type);
    }

    public void setAvailableMoves() {

        var temp = new ArrayList<Move>();
        var ydisp = isBlack ? -1 : 1; // black pawns move down

        // if nothing in front, can move a square forward
        var ok = !(chessBoard.violatesBasicRules(pos, 0, ydisp) || chessBoard.willHitOpponentPiece(pos, 0, ydisp));
        if (ok) {
            // check if can promote
            Action a = chessBoard.canPromote(pos, 0, ydisp) ? Action.PROMOTE : Action.NONE;
            var m = chessBoard.createMove(toString(), pos, 0, ydisp, a);
            temp.add(m);
        }

        // left and right captures, refactored as two-membered for loop
        int xdisp[] = { 1, -1 };
        for (int x : xdisp) {

            var prelimOk = !(chessBoard.violatesBasicRules(pos, x, ydisp));
            if (!prelimOk) {
                continue;
            }
            var canCapture = chessBoard.willHitOpponentPiece(pos, x, ydisp);
            if (canCapture) {
                // check if can promote
                Action a = chessBoard.canPromote(pos, x, ydisp) ? Action.PROMOTE : Action.NONE;
                var m = chessBoard.createMove(toString(), pos, x, ydisp, a);
                temp.add(m);
            }

            // En Passant: can only be done if previous opp move created a passed pawn
            var canEnPassant = chessBoard.canEnPassant(pos, x, ydisp);
            if (canEnPassant) {
                var m = chessBoard.createMove(toString(), pos, x, ydisp, Action.ENPASSANT);
                temp.add(m);
            }
        }

        // can move two squares?
        if (turnFirstMoved == -1) {
            ok = !(chessBoard.violatesBasicRules(pos, 0, 2 * ydisp)
                    || chessBoard.willHitOpponentPiece(pos, 0, 2 * ydisp));
            if (ok) {
                // check if can promote
                Action a = chessBoard.canPromote(pos, 0, 2 * ydisp) ? Action.PROMOTE : Action.NONE;
                var m = chessBoard.createMove(toString(), pos, 0, 2 * ydisp, a);
                temp.add(m);
            }
        }

        availableMoves = temp;
    }

    public ArrayList<Position> getAttackPath(Position KingPos) {
        int xdisp[] = { -1, 1 };
        int y = isBlack ? -1 : 1; // black pawns move down

        for (var x : xdisp) {
            var newX = pos.getX() + x;
            var newY = pos.getY() + y;
            if (newX == KingPos.getX() && newY == KingPos.getY()) {
                return new ArrayList<Position>(); // any attack is unblocked
            }
        }
        return null; // not attacking
    }
}
