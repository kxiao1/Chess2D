package chess;

public class Board {
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
    
    Position[][] squares;

    // All pieces
    public boolean willHitOwnPiece(Position start, int x, int y) {
        return false;
    }

    // All pieces when capturing, pawn when moving forward as well
    public boolean willHitOpponentPiece(Position start, int x, int y) {
        return false;
    }

    // All pieces
    public boolean willBeOOB(Position start, int x, int y) {
        // check for out of bounds

        return false;
    }

    // All pieces
    public boolean willBeChecked(Position start, int x, int y) {

        // a move is not valid if it puts one under check TODO
        
        return false; 
    }

    // applicable to Rook, Bishop, and Queen
    public boolean willPassOtherPieces(Position start, int x, int y) {
        return false; //TODO
    }

    public boolean canCastle(Position start) {
        return false; //TODO
    }

    Board (Position[][] squares) {
        this.squares = squares;
    }
}