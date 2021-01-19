package chess;

import java.util.ArrayList;
import chess.pieces.*;

enum Turn {
    BLACK, WHITE
};

enum Checked {
    BlackChecked, WhiteChecked, NONE
};

class Game {
    Turn turn;
    ArrayList<String> logs;
    ArrayList<Piece> whitePieces;
    ArrayList<Piece> blackPieces;
    ArrayList<Piece> ownPieces;
    ArrayList<Piece> oppPieces;
    ArrayList<Piece> capturedBlack;
    ArrayList<Piece> CapturedWhite;
    Checked checked;
    Board chessBoard;

    Game() {
        turn = Turn.WHITE;
        logs = new ArrayList<String>();
        blackPieces = new ArrayList<Piece>();
        whitePieces = new ArrayList<Piece>();
        ownPieces = whitePieces;
        oppPieces = blackPieces;
        capturedBlack = new ArrayList<Piece>();
        CapturedWhite = new ArrayList<Piece>();
        checked = Checked.NONE;

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
        turn = turn == Turn.BLACK ? Turn.WHITE : Turn.BLACK;
        ownPieces = turn == Turn.WHITE ? whitePieces : blackPieces;
        oppPieces = turn == Turn.WHITE ? blackPieces : whitePieces;
        chessBoard.switchTurns();
    }

    void getAllMoves() {
        for (var p : ownPieces) {
            p.setAvailableMoves();
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
}