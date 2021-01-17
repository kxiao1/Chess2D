package chess;

class Board {
    static int NumX = 8;
    static int NumY = 8;

    static boolean isValidPos(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }

    // check if piece is on black side of the board
    static boolean onBlackSide(Position pos) {
        return pos.getY() >= 4; 
    }

    // get starting piece for a square name
    static Pieces getDefaultPiece(String name) {
        if (name.charAt(1) == '2' || name.charAt(1) == '7') {
            return Pieces.PAWN;
        }
        if (name.charAt(1) != '1' && name.charAt(1) != '8') {
            return Pieces.NONE;
        }
        switch (name.charAt(0)) {
            case 'A':
                return Pieces.ROOK; 
            case 'B':
                return Pieces.KNIGHT;
            case 'C':
                return Pieces.BISHOP;
            case 'D':
                return Pieces.QUEEN;
            case 'E':
                return Pieces.KING;
            case 'F':
                return Pieces.BISHOP;
            case 'G':
                return Pieces.KNIGHT;
            default:
                return Pieces.ROOK;
        }
    }
}