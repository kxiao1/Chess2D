package chess;

import java.util.ArrayList;

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

    private Position[][] squares;
    private Turn turn;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Piece> ownPieces;
    private ArrayList<Piece> oppPieces;

    Board(Position[][] s, ArrayList<Piece> wp, ArrayList<Piece> bp, Turn t) {
        squares = s;
        whitePieces = ownPieces = wp;
        blackPieces = oppPieces = bp;
        turn = t;
    }

    Position[][] getSquares() {
        return squares;
    }

    void switchTurns() {
        turn = turn == Turn.BLACK ? Turn.WHITE : Turn.BLACK;
        ownPieces = turn == Turn.WHITE ? whitePieces : blackPieces;
        oppPieces = turn == Turn.WHITE ? blackPieces : whitePieces;
    }

    // All pieces
    private boolean willHitOwnPiece(Position start, int x, int y) {
        var newX = start.getX() + x;
        var newY = start.getY() + y;
        for (var own : ownPieces) {
            if (own.pos.getX() == newX && own.pos.getY() == newY) {
                return true;
            }
        }
        return false;
    }

    // All pieces
    private boolean willBeOOB(Position start, int x, int y) {
        // check for out of bounds
        var newX = start.getX() + x;
        var newY = start.getY() + y;
        return !isValidPos(newX, newY);
    }

    // All pieces: a move is not valid if it puts one under check TODO
    private boolean willBeChecked(Position start, int x, int y) {
        var canCheck = false;
        // for each of opponent's pieces,
        for (var opp : oppPieces) {
            if (opp.type == Pieces.KING) {
                continue;
            }

            // (1) check that it will not be captured by this move

            // (2) check that it attacks the king

            // (3) check that existing own pieces that are not moved don't block it
            // (4) check that the moved piece does not block it
            for (var own : ownPieces) {

            }
            // uncomment this: canCheck = true;
            break;
        }

        // King: continue
        // Pawn, Knight: (1) + (2)
        // Rook, Bishop, Queen: (1) + (2) + (3) + (4)

        return canCheck;
    }

    // wrapper for above three checks
    public boolean violatesBasicRules(Position start, int x, int y) {
        return willBeOOB(start, x, y) || willHitOwnPiece(start, x, y) || willBeChecked(start, x, y);
    }

    // Pawn when moving forward/ capturing
    public boolean willHitOpponentPiece(Position start, int x, int y) {
        var newX = start.getX() + x;
        var newY = start.getY() + y;
        for (var own : oppPieces) {
            if (own.pos.getX() == newX && own.pos.getY() == newY) {
                return true;
            }
        }
        return false;
    }

    // applicable to Rook, Bishop, and Queen
    public boolean willPassOtherPieces(Position start, int x, int y) {
        return false; // TODO
    }

    public boolean canCastle(Position start) {
        return false; // TODO
    }

    // assumes that the move is legal
    public boolean canPromote(Position start, int x, int y) {
        var newY = start.getY() + y;
        return (newY == 0 || newY == (NumY - 1));
    }

    // needs to be done after a move is made TODO
    public boolean isCheck() {
        // loop through each of one's pieces to see if it attacks the King
        return false;
    }

    public Move createMove(String p, Position start, int x, int y, Action a) {
        var end = squares[start.getX() + x][start.getY() + y];
        return new Move(p, start, end, a);
    }
}