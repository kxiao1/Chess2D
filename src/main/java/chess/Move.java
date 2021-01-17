package chess;
enum Action {
    NONE, PROMOTE,
    CASTLE, ENPASSANT
}

public class Move {
    Position old_pos;
    Position new_pos;
    Action action;
    boolean isCheck; 
}