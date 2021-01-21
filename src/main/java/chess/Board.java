package chess;

import java.util.ArrayList;

public class Board {
    static int NumX = 8;
    static int NumY = 8;

    static boolean isValidPos(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }
    // overloading
    static boolean isValidPos(Position pos) {
        if (pos == null) {
            throw new NullPointerException("Position is null.");
        }
        return isValidPos(pos.getX(), pos.getY());
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
            case 'a':
                return Pieces.ROOK;
            case 'b':
                return Pieces.KNIGHT;
            case 'c':
                return Pieces.BISHOP;
            case 'd':
                return Pieces.QUEEN;
            case 'e':
                return Pieces.KING;
            case 'f':
                return Pieces.BISHOP;
            case 'g':
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
        var newPos = new Position(start.getX() + x, start.getY() + y, null);
        for (var own : ownPieces) {
            if (Position.samePos(own.pos, newPos)) {
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

    // All pieces: a move is not valid if it puts one under check
    public boolean willBeChecked(Position start, int x, int y) {
        var newPos = new Position(start.getX() + x, start.getY() + y, null);
        Position KingPos = null;
        if (start.piece.type == Pieces.KING) {
            KingPos = newPos;
        } else {
            for (var own : ownPieces) {
                if (own.type == Pieces.KING) {
                    KingPos = own.pos;
                    break;
                }
            }
        }
        
        // for each of opponent's pieces,
        // King: continue
        // Pawn, Knight: (1) + (2)
        // Rook, Bishop, Queen: (1) + (2) + (3) + (4)
        for (var opp : oppPieces) {
            if (opp.type == Pieces.KING) {
                continue;
            }

            // (1) check that it will not be captured by this move
            if (Position.samePos(opp.pos, newPos)) {
                continue;
            }

            // (2) check that it attacks the King
            var path = opp.getAttackPath(KingPos);
            if (path == null) {
                continue;
            }

            if (opp.type == Pieces.PAWN || opp.type == Pieces.KNIGHT) {
                return true;
            }

            // (3) check if existing pieces that are not moved can block it
            boolean willBlock = false;
            if (path.size() > 0) {
                for (var pos : path) {
                    var boardPos = squares[pos.getX()][pos.getY()];
                    if (boardPos.piece != null) { // there is an own piece along the path
                        if (!Position.samePos(boardPos, start)) { // this move did not remove the blocker
                            willBlock = true;
                            break;
                        }
                    }
                }
            }

            // (4) check that the moved piece does not block it
            if (!willBlock) {
                for (var pos : path) {
                    var boardPos = squares[pos.getX()][pos.getY()];
                    if (Position.samePos(boardPos, newPos)) {
                        willBlock = true;
                        break;
                    }
                }
            }
            if (!willBlock) {
                return true;
            }
        }

        return false;
    }

    // Wrapper for above checking if a move is OOB, hits own piece, or results in a check
    public boolean violatesBasicRules(Position start, int x, int y) {
        return willBeOOB(start, x, y) || willHitOwnPiece(start, x, y) || willBeChecked(start, x, y);
    }

    // Pawn when moving forward/ capturing
    public boolean willHitOpponentPiece(Position start, int x, int y) {
        var newPos = new Position(start.getX() + x, start.getY() + y, null);
        for (var opp : oppPieces) {
            if (Position.samePos(newPos, opp.pos)) {
                return true;
            }
        }
        return false;
    }

    // Wrapper that is applicable to Rook, Bishop, and Queen
    public boolean cannotReachDestination(Position start, int x, int y) {
        return willBeOOB(start, x, y) || willHitOwnPiece(start, x, y);
    }

    public boolean canCastle(Position start) {
        return false; // TODO
    }

    // assumes that the move is legal
    public boolean canPromote(Position start, int x, int y) {
        var newY = start.getY() + y;
        return (newY == 0 || newY == (NumY - 1));
    }

    // needs to be done after a move is made and before the turn changes
    public boolean isCheck() {
        // loop through each of ownPieces to see if it attacks the opponent's King
        Position KingPos = null;
        for (var opp: oppPieces) {
            if (opp.type == Pieces.KING) {
                KingPos = opp.pos;
            }
        }

        // for each of ownPieces,
        for (var own : ownPieces) {
            if (own.type == Pieces.KING) {
                continue;
            } 

            // check that it attacks the King
            var path = own.getAttackPath(KingPos);
            if (path == null) {
                continue;
            }

            if (own.type == Pieces.PAWN || own.type == Pieces.KNIGHT) {
                return true;
            }
            
            // check if a piece blocks along the path
            boolean willBlock = false;
            if (path.size() > 0) {
                for (var pos : path) {
                    var boardPos = squares[pos.getX()][pos.getY()];
                    if (boardPos.piece != null) { // there is an own piece along the path
                        willBlock = true;
                        break;
                    }
                }
            }
            if (!willBlock) {
                return true;
            }
        }

        return false;
    }

    public Move createMove(String p, Position start, int x, int y, Action a) {
        var end = squares[start.getX() + x][start.getY() + y];
        return new Move(p, start, end, a);
    }
}