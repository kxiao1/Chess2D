package chess;
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
    Position[][] squares;

    Game() {
        turn = Turn.WHITE;
        logs = new ArrayList<String>();
        CapturedBlack = new ArrayList<Piece>();
        CapturedWhite = new ArrayList<Piece>();
        checked = Checked.NONE;
        squares = new Position[Board.NumX][];
        for (int x = 0; x < Board.NumX; ++x) {
            squares[x] = new Position[Board.NumY];
            for (int y = 0; y < Board.NumY; ++y) {
                squares[x][y] = new Position(x, y, null); 
                // change null to actual piece!
            }
        } 
    }
}