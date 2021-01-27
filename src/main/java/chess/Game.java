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
    private ArrayList<String> logs;
    ArrayList<Piece> whitePieces;
    ArrayList<Piece> blackPieces;
    private ArrayList<Piece> ownPieces;
    private ArrayList<Piece> oppPieces;
    private ArrayList<Piece> capturedBlack;
    private ArrayList<Piece> capturedWhite;
    private Move uncastleKingMove;
    private Piece capturedPieceToRestore;
    private boolean isChecked;
    private boolean isCheckmated;
    private boolean isBeingUndone;
    private boolean isNoOp;
    private Board chessBoard;

    Game() {
        turn = Turn.WHITE;
        turnNo = 1;
        logs = new ArrayList<String>();
        blackPieces = new ArrayList<Piece>();
        whitePieces = new ArrayList<Piece>();
        ownPieces = whitePieces;
        oppPieces = blackPieces;
        capturedBlack = new ArrayList<Piece>();
        capturedWhite = new ArrayList<Piece>();
        uncastleKingMove = null;
        capturedPieceToRestore = null;
        isChecked = false;
        isCheckmated = false;
        isBeingUndone = false;
        isNoOp = false;

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

    // Return true if the current player has moves to make
    boolean getAllMoves() {

        int moveCount = 0;
        for (var p : ownPieces) {
            p.setAvailableMoves();
            if (p.type == Pieces.KING && !isChecked) {
                var m = chessBoard.makeCastlingMove(p.pos);
                if (m != null) {
                    p.getAvailableMoves().addAll(m);
                }
            }
            moveCount += p.getAvailableMoves().size();
        }

        // prevent false checkmates during an undo sequence
        return (isBeingUndone || moveCount > 0);
    }

    Position[][] getSquares() {
        return chessBoard.getSquares();
    }

    String printBoard() {
        return chessBoard.toString();
    }

    boolean isCheck() {
        if (isBeingUndone) {
            return false;
        } 
        isChecked = chessBoard.isCheck();
        if (isChecked && !isNoOp) {
            var lastLogs = logs.remove(logs.size() - 1);
            logs.add(lastLogs.substring(0, lastLogs.length() - 1) + "+ ");
        }
        return isChecked;
    }

    boolean isCheckmated() {
        return isCheckmated;
    }

    void addToLogs(Move m) {
        var mName = m.toString();
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
        lastEntry = lastEntry.substring(0, lastEntry.length() - 2) + "# " + (turn == Turn.BLACK ? "1-0" : "0-1");
        logs.add(lastEntry);
        isCheckmated = true;
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

    // Account for the extra turn increment after NOP move
    void switchTurnNoOp() {
        turnNo--;

        // end no-op sequence
        isNoOp = false;
    }

    void resetPieceStateUndo(Piece p) {
        // put the captured piece (if any) back to the board
        if (capturedPieceToRestore != null) {
            var pos = capturedPieceToRestore.pos;
            pos.piece = capturedPieceToRestore;
            if (pos.piece.isBlack) {
                blackPieces.add(pos.piece);
            } else {
                whitePieces.add(pos.piece);
            }
            capturedPieceToRestore = null;
        }
        
        // complete the undoing sequence
        isBeingUndone = false;

        // start the no-op sequence
        isNoOp = true;
    }

    Piece getCapturedPiece() {
        return capturedPieceToRestore;
    }

    Move getUncastleKingMove() {
        return uncastleKingMove;
    }

    // Make special NOP with same starting and ending position
    Move getNoOpKingMove() {
        Piece king = null;
        Position kingPos = null;
        for (var own : ownPieces) {
            if (own.type == Pieces.KING) {
                king = own;
                kingPos = king.pos;
            }
        }
        return new Move(king.toString(), kingPos, kingPos, Action.NONE);
    }

    Move undoMove() {
        if (logs.size() == 0) {
            // cannot undo from first move
            return null;
        }

        // start the undoing sequence
        isBeingUndone = true;

        // switch the player here too, because we started this undo in the
        // other player's turn
        switchTurn();

        // go back to the correct turn number
        turnNo--;

        // use string parsing
        var mText = logs.remove(logs.size() - 1);

        if (isCheckmated) {
            isCheckmated = false;
            mText = mText.substring(0, mText.length() - 3);
        }

        // strip off trailing space
        mText = mText.substring(0, mText.length() - 1);

        // a checkmate is also a check (comes with an extra character)
        if (isChecked) {
            mText = mText.substring(0, mText.length() - 1);
        }

        // last move was castling: unmove King manually here and return rook move
        // the app will process the rook move and render the reverted King move
        int isCastle = 0;
        if (mText.substring(mText.length() - 5).equals("O-O-O")) {
            isCastle = 2;
            if (turn == Turn.BLACK) {
                // push the white half of the turn back to logs
                mText = mText.substring(0, mText.length() - 5);
                logs.add(mText);
            }
        }
        if (mText.substring(mText.length() - 3).equals("O-O")) {
            isCastle = 1;
            if (turn == Turn.BLACK) {
                // push the white half of the turn back to logs
                mText = mText.substring(0, mText.length() - 3);
                logs.add(mText);
            }
        }
        if (isCastle > 0) {
            Position KingPosEnd = null;
            for (var own : ownPieces) {
                if (own.type == Pieces.KING) {
                    KingPosEnd = own.pos;
                }
            }

            // unmove the King
            var king = KingPosEnd.piece;
            king.turnFirstMoved = -1;
            KingPosEnd.piece = null;

            Position KingPosStart = chessBoard.getSquares()[4][KingPosEnd.getY()];
            KingPosStart.piece = king;

            king.pos = KingPosStart;

            // prepare for App to request this move
            uncastleKingMove = new Move(king.toString(), KingPosEnd, KingPosStart, Action.NONE);

            // return rook Move
            Position rookPosEnd = null;
            Move rookMove = null;
            if (isCastle == 1) { // O-O
                rookPosEnd = chessBoard.getSquares()[5][KingPosEnd.getY()];
                rookMove = chessBoard.createMove(rookPosEnd.piece.toString(), rookPosEnd, 2, 0, Action.UNCASTLE);
                rookPosEnd.piece.turnFirstMoved = -1;
                return rookMove;
            } else { // O-O-O
                rookPosEnd = chessBoard.getSquares()[3][KingPosEnd.getY()];
                rookMove = chessBoard.createMove(rookPosEnd.piece.toString(), rookPosEnd, -3, 0, Action.UNCASTLE);
                rookPosEnd.piece.turnFirstMoved = -1;
                return rookMove;
            }
        }

        var isPromote = false;
        if (mText.substring(mText.length() - 2).equals("=P")) {
            mText = mText.substring(0, mText.length() - 2);
            isPromote = true;
        }

        var endPosStr = mText.substring(mText.length() - 2);
        var endPos = chessBoard.getSquares()[Position.getXFromString(endPosStr)][Position.getYFromString(endPosStr)];
        mText = mText.substring(0, mText.length() - 2);

        var isCapture = false;
        if (mText.charAt(mText.length() - 1) == 'x') {
            mText = mText.substring(0, mText.length() - 1);
            isCapture = true;
        }

        var startPosStr = mText.substring(mText.length() - 2);
        var startPos = chessBoard.getSquares()[Position.getXFromString(startPosStr)][Position
                .getYFromString(startPosStr)];

        var piece = endPos.piece;

        if (piece.turnFirstMoved == turnNo) {
            // mark piece as unmoved
            piece.turnFirstMoved = -1;
        }

        // push the white half of the turn back to logs
        if (turn == Turn.BLACK) {
            var numToCut = piece.type == Pieces.PAWN ? 2 : 3;
            mText = mText.substring(0, mText.length() - numToCut);
            logs.add(mText);
        }

        // un-capture if needed, includes un-En Passant case
        if (isCapture) {
            var capPieces = turn == Turn.WHITE ? capturedBlack : capturedWhite;
            Piece capPiece = capPieces.remove(capPieces.size() - 1);
            capturedPieceToRestore = capPiece;
        }

        // un-promote if needed TODO
        if (isPromote) {
            // restore the pawn, adjust its state etc.
            if (isCapture) {
                // return (... UNPROMOTEandUNCAPTURE)
            } else {
                // return (... UNPROMOTE)
            }
        }

        // caotured piece is restored later

        // return a new move
        if (isCapture) {
            return new Move(piece.toString(), endPos, startPos, Action.UNCAPTURE);
        }
        return new Move(piece.toString(), endPos, startPos, Action.NONE);
    }

    // Updates state and returns captured piece if any
    Piece makeMoveAndCapture(Move m) {
        // if start and end pos are the same, the NOP move should not change state
        if (Position.samePos(m.old_pos, m.new_pos)) {
            return null;
        }

        var startPos = m.old_pos;
        var p = startPos.piece;
        if (p.turnFirstMoved == -1 && !isBeingUndone) {
            p.turnFirstMoved = turnNo;
        }
        var endPos = m.new_pos;
        startPos.piece = null;

        var oppPiece = endPos.piece;
        if (oppPiece != null) {
            oppPieces.remove(oppPiece);
            if (oppPiece.isBlack) {
                capturedBlack.add(oppPiece);
            } else {
                capturedWhite.add(oppPiece);
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
        switch (KingPos.getX()) {
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
        rook.turnFirstMoved = turnNo;
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