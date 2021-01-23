package chess;

public class Move {
    String pieceName;
    Position old_pos;
    Position new_pos;
    Action action;

    String name;

    String getAbbrev(String pieceName) {
        // remove the _white/ _black
        var p = pieceName.substring(0,pieceName.length() - 6);
        switch (p) {
            case "KNIGHT":
                return "N";
            case "PAWN":
                return "";
            default:
                return p.substring(0, 1);
        }
    }

    Move(String p, Position o, Position n, Action a) {
        if (p.length() == 0) {
            throw new IllegalArgumentException("Give the name of the piece that was moved.");
        }
        if (!Board.isValidPos(o)) {
            throw new NullPointerException("Old position is not valid.");
        }
        if (!Board.isValidPos(n)) {
            throw new NullPointerException("New position is not valid.");
        }

        pieceName = p;
        old_pos = o;
        new_pos = n;
        action = a;

        if (action == Action.CASTLE) {
            if (new_pos.getX() > old_pos.getX()) {
                // Kingside Castling
                name = "O-O";
            } else {
                name = "O-O-O";
            }
            return;
        }

        //create 'verbose' algebraic notation, do not indicate check/ checkmate
        var pAbbrev = getAbbrev(pieceName);
        var start = old_pos.toString();
        var isCapture = (new_pos.piece != null || action == Action.ENPASSANT) ? "x" : "";
        var end = new_pos.toString();
        var isPromote = action == Action.PROMOTE ? "=Q" : "";
        name = pAbbrev + start + isCapture + end + isPromote;
    }

    public String toString() {
        return name;
    }
}