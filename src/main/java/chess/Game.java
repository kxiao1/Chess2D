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
    ArrayList<Piece> WhitePieces;
    ArrayList<Piece> BlackPieces;
    ArrayList<Piece> CapturedBlack;
    ArrayList<Piece> CapturedWhite;
    Checked checked;
    Board chessBoard;

    Game() {
        turn = Turn.WHITE;
        logs = new ArrayList<String>();
        BlackPieces = new ArrayList<Piece>();
        WhitePieces = new ArrayList<Piece>();
        CapturedBlack = new ArrayList<Piece>();
        CapturedWhite = new ArrayList<Piece>();
        checked = Checked.NONE;
        
        var squares = new Position[Board.NumX][];
        chessBoard = new Board(squares); 
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
                    if (pos.isBlack) {
                        newPiece.ownPieces = BlackPieces;
                        newPiece.oppPieces = WhitePieces;
                        BlackPieces.add(newPiece);
                    } else {
                        newPiece.ownPieces = WhitePieces;
                        newPiece.oppPieces = BlackPieces;
                        WhitePieces.add(newPiece);
                    }
                }
                squares[x][y] = pos; 
            }
        }
    }

    void switchTurn() {
        turn = turn == Turn.BLACK ? Turn.WHITE : Turn.BLACK;
    }

    void getAllMoves() {
        var pieces = turn == Turn.BLACK ? BlackPieces : WhitePieces;
        for (var p : pieces) {
            p.setAvailableMoves();
        } 
    }

    // displacement
}