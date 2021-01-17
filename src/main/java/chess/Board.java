package chess;
class Board {
    static int NumX = 8;
    static int NumY = 8;

    static boolean isValidPos(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }
}