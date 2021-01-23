package chess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import chess.pieces.*;

enum Turn {
    BLACK, WHITE
};
class Game {
    Turn turn;
    int turnNo;
    ArrayList<String> logs;
    ArrayList<Piece> whitePieces;
    ArrayList<Piece> blackPieces;
    ArrayList<Piece> ownPieces;
    ArrayList<Piece> oppPieces;
    ArrayList<Piece> capturedBlack;
    ArrayList<Piece> CapturedWhite;
    boolean checked;
    Board chessBoard;

    Game() {
        turn = Turn.WHITE;
        turnNo = 1;
        logs = new ArrayList<String>();
        blackPieces = new ArrayList<Piece>();
        whitePieces = new ArrayList<Piece>();
        ownPieces = whitePieces;
        oppPieces = blackPieces;
        capturedBlack = new ArrayList<Piece>();
        CapturedWhite = new ArrayList<Piece>();
        checked = false;

        var squares = new Position[Board.NumX][];
        chessBoard = new Board(squares, whitePieces, blackPieces, turn);
        for (int x = 0; x < Board.NumX; ++x) {
            squares[x] = new Position[Board.NumY];
            for (int y = 0; y < Board.NumY; ++y) {
                var pos = new Position(x, y, null);
                var pieceName = Board.getDefaultPiece(pos.toString());
                Piece newPiece = null;
                switch (pieceName) {
                    case BISHOP:
                        newPiece = new Bishop(pos, pieceName);
                        break;
                    case KING:
                        newPiece = new King(pos, pieceName);
                        break;
                    case KNIGHT:
                        newPiece = new Knight(pos, pieceName);
                        break;
                    case PAWN:
                        newPiece = new Pawn(pos, pieceName);
                        break;
                    case QUEEN:
                        newPiece = new Queen(pos, pieceName);
                        break;
                    case ROOK:
                        newPiece = new Rook(pos, pieceName);
                        break;
                    default:
                }
                if (newPiece != null) {
                    newPiece.chessBoard = chessBoard;
                    pos.piece = newPiece;
                    if (newPiece.isBlack) {
                        newPiece.ownPieces = blackPieces;
                        newPiece.oppPieces = whitePieces;
                        blackPieces.add(newPiece);
                    } else {
                        newPiece.ownPieces = whitePieces;
                        newPiece.oppPieces = blackPieces;
                        whitePieces.add(newPiece);
                    }
                }
                squares[x][y] = pos;
            }
        }
    }

    void switchTurn() {
        if (turn == Turn.BLACK) {
            turnNo++;
        }
        turn = turn == Turn.BLACK ? Turn.WHITE : Turn.BLACK;
        ownPieces = turn == Turn.WHITE ? whitePieces : blackPieces;
        oppPieces = turn == Turn.WHITE ? blackPieces : whitePieces;
        chessBoard.switchTurns();
    }

    boolean getAllMoves() {
        int moveCount = 0;
        for (var p : ownPieces) {
            p.setAvailableMoves();
            if (p.type == Pieces.KING && !isCheck()) {
                var m = chessBoard.makeCastlingMove(p.pos);
                if (m != null) {
                    p.getAvailableMoves().addAll(m);
                }
            }
            moveCount += p.getAvailableMoves().size();
        }
        return (moveCount > 0);
    }

    Position[][] getSquares() {
        return chessBoard.getSquares();
    }

    boolean isCheck() {
        var isCheck = chessBoard.isCheck();
        checked = isCheck;
        return isCheck;
    }

    void addToLogs(Move m) {
        var mName = m.toString();
        if (checked) {
            mName = mName.concat("+");
        }
        if (turn == Turn.WHITE) {
            var newEntry = turnNo + ". " + mName + " "; 
            logs.add(newEntry);
        } else {
            var currEntry = logs.remove(logs.size() - 1);
            var appendedEntry = currEntry + mName + " ";
            logs.add(appendedEntry);
        }
    }

    void indicateCheckmate() {
        var lastEntry = logs.remove(logs.size() - 1);
        lastEntry = lastEntry.substring(0, lastEntry.length() - 2) + "# " 
                    + (turn == Turn.BLACK ? "1-0" : "0-1");
        logs.add(lastEntry);
    }

    void saveLogs() {
        try (var log = new BufferedWriter(new FileWriter("logs.txt"))) {
            for (var line : logs) {
                log.write(line); 
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    // Updates state and returns captured piece if any
    Piece makeMoveAndCapture(Move m) {
        var startPos = m.old_pos;
        var p = startPos.piece;
        if (!p.hasMoved) {
            p.hasMoved = true;
        }
        var endPos = m.new_pos;
        startPos.piece = null;

        var oppPiece = endPos.piece;
        if (oppPiece != null) {
            oppPieces.remove(oppPiece);
            if (oppPiece.isBlack) {
                capturedBlack.add(oppPiece);
            } else {
                CapturedWhite.add(oppPiece);
            }
        }
        endPos.piece = p;

        p.pos = endPos;

        return oppPiece;
    }

    // Given the final position of the King, move the Rook as well
    Move makeCastle(Position KingPos) {
        Move rookMove = null;
        Position rookPosStart = null;
        switch(KingPos.getX()) {
            case 2: 
                rookPosStart = chessBoard.getSquares()[0][KingPos.getY()];
                rookMove = chessBoard.createMove(rookPosStart.piece.toString(), rookPosStart, 3, 0, Action.NONE);
                break;
            case 6:
                rookPosStart = chessBoard.getSquares()[7][KingPos.getY()];
                rookMove = chessBoard.createMove(rookPosStart.piece.toString(), rookPosStart, -2, 0, Action.NONE);
                break;
            default:
                throw new IllegalArgumentException("The King is in the wrong column.");
        }
        var rook = rookPosStart.piece;
        rook.hasMoved = true;
        var rookPosEnd = rookMove.new_pos;
        rookPosStart.piece = null;
        rookPosEnd.piece = rook;
        rook.pos = rookPosEnd;
        return rookMove;
    }

    // Promotions TODO
    Piece makeNewPiece(Position pos) {
        // similar to a capture, but don't add to captured list

        // return the new Queen
        return null;
    }

    // En Passant TODO
    Piece makeEnPassant(Move m) {
        
        // return captured piece
        return null;
    }
}