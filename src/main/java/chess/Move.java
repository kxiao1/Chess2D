package chess;

public class Move {
    String pieceName;
    Position old_pos;
    Position new_pos;
    Action action;

    Move(String p, Position o, Position n, Action a) {
        pieceName = p;
        old_pos = o;
        new_pos = n;
        action = a;
    }

    // TODO: add name to move
}