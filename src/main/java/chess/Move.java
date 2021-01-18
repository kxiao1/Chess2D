package chess;

enum Action {
    NONE, PROMOTE, CASTLE, ENPASSANT
}

public class Move {
    Position old_pos;
    Position new_pos;
    Action action;
    boolean isCheck;

    Move(Position o, Position n, Action a, boolean i) {
        old_pos = o;
        new_pos = n;
        action = a;
        isCheck = i;
    }
}