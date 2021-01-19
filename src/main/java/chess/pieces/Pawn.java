package chess.pieces;

import chess.*;
import java.util.ArrayList;

public class Pawn extends Piece {

    public Pawn(Position pos, Pieces type) {
        super(pos, type);
    }

    public void setAvailableMoves() {
        // TODO
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

        // left and right captures
        ok = !(chessBoard.violatesBasicRules(pos, 1, ydisp)) && chessBoard.willHitOpponentPiece(pos, 1, ydisp);
        if (ok) {
            // check if can promote
            Action a = chessBoard.canPromote(pos, 1, ydisp) ? Action.PROMOTE : Action.NONE;
            var m = chessBoard.createMove(toString(), pos, 1, ydisp, a);
            temp.add(m);
        }
        ok = !(chessBoard.violatesBasicRules(pos, -1, ydisp)) && chessBoard.willHitOpponentPiece(pos, -1, ydisp);
        if (ok) {
            // check if can promote
            Action a = chessBoard.canPromote(pos, -1, ydisp) ? Action.PROMOTE : Action.NONE;
            var m = chessBoard.createMove(toString(), pos, -1, ydisp, a);
            temp.add(m);
        }

        // passed pawn?
        if (!hasMoved) {
            ok = !(chessBoard.violatesBasicRules(pos, 0, 2 * ydisp)
                    || chessBoard.willHitOpponentPiece(pos, 0, 2 * ydisp));
            if (ok) {
                // check if can promote
                Action a = chessBoard.canPromote(pos, 0, 2 * ydisp) ? Action.PROMOTE : Action.NONE;
                var m = chessBoard.createMove(toString(), pos, 0, 2 * ydisp, a);
                temp.add(m);
            }
        }

        // En Passant? TODO
        availableMoves = temp;
    }
}
