package chess;

public class Move {
    String pieceName;
    Position old_pos;
    Position new_pos;
    Action action;

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
    }

    // TODO: add name to move
}