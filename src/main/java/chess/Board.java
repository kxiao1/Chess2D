package chess;
class Board {
    Position[][] squares;

    static boolean isValidPos(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }
}