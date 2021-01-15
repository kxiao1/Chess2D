package chess.src.main.java;

import java.util.ArrayList;

enum Turn {
    BLACK, WHITE
};

enum Checked {
    BlackChecked, WhiteChecked, NONE
};

class Game {
    Turn turn;
    ArrayList<String> logs;
    ArrayList<Piece> CapturedBlack;
    ArrayList<Piece> CapturedWhite;
    Checked checked;
}