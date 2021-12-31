package chess;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
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

    private ArrayList<Piece> whitePieces = new ArrayList<Piece>();
    private ArrayList<Piece> blackPieces = new ArrayList<Piece>();
    private ArrayList<Piece> ownPieces = whitePieces;
    private ArrayList<Piece> oppPieces = blackPieces;
    private ArrayList<Piece> capturedBlack = new ArrayList<Piece>();
    private ArrayList<Piece> capturedWhite = new ArrayList<Piece>();
    private ArrayList<Piece> promotedWhite = new ArrayList<Piece>();
    private ArrayList<Piece> promotedBlack = new ArrayList<Piece>();

    private Move uncastleKingMove;
    private Piece capturedPieceToRestore;
    private boolean isChecked;
    private boolean isCheckmated;
    private boolean isStalemate;
    private boolean isResigned;
    private boolean isBeingUndone;
    private boolean isNoOp;
    private Board chessBoard;

    Game() {
        turn = Turn.WHITE;
        turnNo = 1;
        logs = new ArrayList<String>();

        var squares = new Position[Board.NumX][];
        chessBoard = new Board(squares, this);
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
                        blackPieces.add(newPiece);
                    } else {
                        whitePieces.add(newPiece);
                    }
                }
                squares[x][y] = pos;
            }
        }
    }

    /* Set state */

    void switchTurn() {
        if (turn == Turn.BLACK) {
            turnNo++;
        }
        turn = turn == Turn.BLACK ? Turn.WHITE : Turn.BLACK;
        ownPieces = turn == Turn.WHITE ? whitePieces : blackPieces;
        oppPieces = turn == Turn.WHITE ? blackPieces : whitePieces;
    }

    // Account for the extra turn increment after NOP move
    void switchTurnNoOp() {
        turnNo--;
    }

    // end no-op sequence
    void endNoOp() {
        isNoOp = false;
    }

    void indicateResign() {
        isResigned = true;
    }

    void indicateCheckmate() {
        var lastEntry = logs.remove(logs.size() - 1);
        lastEntry = lastEntry.substring(0, lastEntry.length() - 2) + "# ";
        logs.add(lastEntry);
    }

    /****************************** Getters **********************************/

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
        if (moveCount == 0 && !isBeingUndone) {
            if (isChecked) {
                isCheckmated = true;
            } else {
                isStalemate = true;
            }
            return false;
        }

        return true;
    }

    Position[][] getSquares() {
        return chessBoard.getSquares();
    }

    ArrayList<Piece> getOwnPieces() {
        return ownPieces;
    }

    ArrayList<Piece> getOppPieces() {
        return oppPieces;
    }

    /* Boolean checks */

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

    boolean canRedo() {
        if (logs.size() < turnNo) {
            return false; // we have not played this move yet
        }
        String[] mText = logs.get(turnNo - 1).split(" ");
        return turn == Turn.WHITE || mText.length > 1;
    }

    /*************************** Manipulate logs *****************************/

    void addToLogs(Move m) {
        var mName = m.toString();
        if (turn == Turn.WHITE) {
            // truncate existing log
            logs = new ArrayList<String>(logs.subList(0, turnNo - 1));
            var newEntry = mName + " ";
            logs.add(newEntry);
        } else {
            // truncate existing log
            logs = new ArrayList<String>(logs.subList(0, turnNo));
            var currEntry = logs.remove(logs.size() - 1);
            var appendedEntry = currEntry + mName + " ";
            logs.add(appendedEntry);
        }
    }

    void saveLogs() {
        try (var log = new BufferedWriter(new FileWriter("logs.txt"))) {
            log.write("[Event \"Own\"]\n\n");
            if (logs.size() == 0) {
                System.out.println("No logs thus far.");
                return;
            }
            int count = 1;
            for (var line : logs) {
                log.write("" + count + "." + line);
                count++;
            }
            if (isCheckmated || isStalemate || isResigned) {
                if (isCheckmated || isResigned) {
                    log.write(turn == Turn.BLACK ? " 1-0" : " 0-1");
                } else {
                    log.write(" 1/2-1/2");
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    boolean readFile(File f) {
        try (var logReader = new BufferedReader(new FileReader(f))) {
            System.out.println("File Reading #Todo");
            String firstLine = logReader.readLine();
            if (firstLine == null) {
                // throw new IllegalArgumentException("Empty log file");
                System.out.println("Empty log file. Please retry.");
                return false;
            }
            logs = new ArrayList<String>();
            String log_string;
            if (firstLine.contains("Own")) {
                System.out.println("Verbose Logs");
            } else {
                throw new IllegalArgumentException(
                        "PGN logs not supported at this time.");
            }
            for (log_string = logReader.readLine(); log_string
                    .contains("["); log_string = logReader.readLine()) {
                System.out.println(log_string);
            }
            String buff = "";
            int count = 0;
            while ((log_string = logReader.readLine()) != null) {
                for (var str : log_string.split(" ")) {
                    if (str.length() == 0 || str.contains("1-0")
                            || str.contains("1-0") || str.contains("0-1")
                            || str.contains("{") || str.contains("(")
                            || str.contains(";")) {
                        continue;
                    }
                    int idx = str.indexOf('.');
                    if (idx > 0) {
                        str = str.substring(idx + 1);
                    }
                    if (count == 0) {
                        buff = str + " ";
                    } else {
                        logs.add(buff + str + " ");
                    }
                    count = (count + 1) % 2;
                }
            }
            if (count == 1) {
                logs.add(buff);
            }
            for (var check : logs) {
                System.out.println(check);
            }
            if (logs.size() == 0) {
                System.out.println("No logs provided.");
                return false;
            }
            return true;
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e);
            return false;
        }
    }

    Piece getCapturedPiece() {
        return capturedPieceToRestore;
    }

    Move getUncastleKingMove() {
        return uncastleKingMove;
    }

    /********************** Undo/ Redo-related logic *************************/

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

    // Make special NOP with same starting and ending position
    Move createNoOpKingMove() {
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

    Move createRedoMove() {
        if (!canRedo()) {
            return null;
        }
        String mText = logs.get(turnNo - 1).split(" ")[turn == Turn.WHITE ? 0
                : 1];

        // basically the same as undoing, without the 'un's

        // remove additional character from a check(mate)
        if (mText.charAt(mText.length() - 1) == '+'
                || mText.charAt(mText.length() - 1) == '#') {
            mText = mText.substring(0, mText.length() - 1);
        }

        // handle castling
        if (mText.equals("O-O-O") || mText.equals("O-O")) {

            Position KingPosStart = null;
            for (var own : ownPieces) {
                if (own.type == Pieces.KING) {
                    KingPosStart = own.pos;
                }
            }
            var king = KingPosStart.piece;

            // castle move is king move
            if (mText.equals("O-O-O")) {
                return chessBoard.createMove(king.toString(), KingPosStart, -2,
                        0, Action.CASTLE);
            }
            if (mText.equals("O-O")) {
                return chessBoard.createMove(king.toString(), KingPosStart, 2,
                        0, Action.CASTLE);
            }
        }

        var isPromote = false;
        if (mText.substring(mText.length() - 2).equals("=Q")) {
            mText = mText.substring(0, mText.length() - 2);
            isPromote = true;
        }

        var endPosStr = mText.substring(mText.length() - 2);
        var endPos = chessBoard.getSquares()[Position
                .getXFromString(endPosStr)][Position.getYFromString(endPosStr)];
        mText = mText.substring(0, mText.length() - 2);

        boolean isCapture = false;
        if (mText.charAt(mText.length() - 1) == 'x') {
            mText = mText.substring(0, mText.length() - 1);
            isCapture = true;
        }

        var startPosStr = mText.substring(mText.length() - 2);
        var startPos = chessBoard.getSquares()[Position.getXFromString(
                startPosStr)][Position.getYFromString(startPosStr)];

        var piece = startPos.piece;
        if (isPromote) {
            return new Move(piece.toString(), startPos, endPos, Action.PROMOTE);
        }
        if (piece.type == Pieces.PAWN && isCapture && endPos.piece == null) {
            return new Move(piece.toString(), startPos, endPos,
                    Action.ENPASSANT);
        }

        return new Move(piece.toString(), startPos, endPos, Action.NONE);
    }

    Move createUndoMove() {
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
        String mText = logs.get(turnNo - 1).split(" ")[turn == Turn.WHITE ? 0
                : 1];

        isCheckmated = isStalemate = isResigned = false;

        // remove additional character from a check(mate)
        if (mText.charAt(mText.length() - 1) == '+'
                || mText.charAt(mText.length() - 1) == '#') {
            mText = mText.substring(0, mText.length() - 1);
        }
        System.out.println("Move to undo: '" + mText + "'");

        // last move was castle: unmove King manually here and return rook move
        // the app will process the rook move and render the reverted King move

        if (mText.equals("O-O-O") || mText.equals("O-O")) {
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

            Position KingPosStart = chessBoard.getSquares()[4][KingPosEnd
                    .getY()];
            KingPosStart.piece = king;

            king.pos = KingPosStart;

            // prepare for App to request this move
            uncastleKingMove = new Move(king.toString(), KingPosEnd,
                    KingPosStart, Action.NONE);

            // return rook Move
            Position rookPosEnd = null;
            Move rookMove = null;
            if (mText.equals("O-O")) {
                rookPosEnd = chessBoard.getSquares()[5][KingPosEnd.getY()];
                rookMove = chessBoard.createMove(rookPosEnd.piece.toString(),
                        rookPosEnd, 2, 0, Action.UNCASTLE);
                rookPosEnd.piece.turnFirstMoved = -1;
                return rookMove;
            } else { // O-O-O
                rookPosEnd = chessBoard.getSquares()[3][KingPosEnd.getY()];
                rookMove = chessBoard.createMove(rookPosEnd.piece.toString(),
                        rookPosEnd, -3, 0, Action.UNCASTLE);
                rookPosEnd.piece.turnFirstMoved = -1;
                return rookMove;
            }
        }

        var isPromote = false;
        if (mText.substring(mText.length() - 2).equals("=Q")) {
            mText = mText.substring(0, mText.length() - 2);
            isPromote = true;
        }

        var endPosStr = mText.substring(mText.length() - 2);
        var endPos = chessBoard.getSquares()[Position
                .getXFromString(endPosStr)][Position.getYFromString(endPosStr)];
        mText = mText.substring(0, mText.length() - 2);

        var isCapture = false;
        if (mText.charAt(mText.length() - 1) == 'x') {
            mText = mText.substring(0, mText.length() - 1);
            isCapture = true;
        }

        var startPosStr = mText.substring(mText.length() - 2);
        var startPos = chessBoard.getSquares()[Position.getXFromString(
                startPosStr)][Position.getYFromString(startPosStr)];

        var piece = endPos.piece;

        if (piece.turnFirstMoved == turnNo) {
            // mark piece as unmoved
            piece.turnFirstMoved = -1;
        }

        // un-capture if needed, includes un-En Passant case
        if (isCapture) {
            var capPieces = turn == Turn.WHITE ? capturedBlack : capturedWhite;
            Piece capPiece = capPieces.remove(capPieces.size() - 1);
            capturedPieceToRestore = capPiece;
        }

        // un-promote if needed
        if (isPromote) {
            // restore the pawn, adjust its state etc.
            var Queen = piece;
            ownPieces.remove(Queen);
            var pawn = Queen.isBlack
                    ? promotedBlack.remove(promotedBlack.size() - 1)
                    : promotedWhite.remove(promotedWhite.size() - 1);

            pawn.pos.piece = pawn;
            ownPieces.add(pawn);

            if (isCapture) {
                return new Move(pawn.toString(), endPos, startPos,
                        Action.UNPROMOTEandUNCAPTURE);
            } else {
                return new Move(pawn.toString(), endPos, startPos,
                        Action.UNPROMOTE);
            }
        }

        // captured piece is restored later

        // return a new move
        if (isCapture) {
            return new Move(piece.toString(), endPos, startPos,
                    Action.UNCAPTURE);
        }
        return new Move(piece.toString(), endPos, startPos, Action.NONE);
    }

    /***************************** Regular moves ******************************/

    // Updates state and returns captured piece if any
    Piece makeMoveAndCapture(Move m) {
        // Since start and end pos are the same, the NOP move should not change
        // state
        if (isNoOp) {
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
            rookMove = chessBoard.createMove(rookPosStart.piece.toString(),
                    rookPosStart, 3, 0, Action.NONE);
            break;
        case 6:
            rookPosStart = chessBoard.getSquares()[7][KingPos.getY()];
            rookMove = chessBoard.createMove(rookPosStart.piece.toString(),
                    rookPosStart, -2, 0, Action.NONE);
            break;
        default:
            throw new IllegalArgumentException(
                    "The King is in the wrong column.");
        }
        var rook = rookPosStart.piece;
        rook.turnFirstMoved = turnNo;
        var rookPosEnd = rookMove.new_pos;
        rookPosStart.piece = null;
        rookPosEnd.piece = rook;
        rook.pos = rookPosEnd;
        return rookMove;
    }

    // Promotion to Queen
    Piece makeNewQueen(Position pos) {
        // similar to a capture, but don't add to captured list
        var pawn = pos.piece;
        ownPieces.remove(pawn);
        pos.piece = null;

        if (pawn.isBlack) {
            promotedBlack.add(pawn);
        } else {
            promotedWhite.add(pawn);
        }

        Piece queen = new Queen(pos, Pieces.QUEEN, pawn.isBlack);
        queen.chessBoard = this.chessBoard;
        pos.piece = queen;
        ownPieces.add(queen);

        // return the new Queen
        return queen;
    }

    // En Passant
    Piece makeEnPassant(Move m) {
        var targetX = m.new_pos.getX();
        var targetY = m.old_pos.getY();
        var capSq = chessBoard.getSquares()[targetX][targetY];
        if (capSq.piece == null || capSq.piece.type != Pieces.PAWN) {
            throw new IllegalArgumentException(
                    "There is no pawn to capture En Passant.");
        }
        var pawn = capSq.piece;
        oppPieces.remove(pawn);
        if (pawn.isBlack) {
            capturedBlack.add(pawn);
        } else {
            capturedWhite.add(pawn);
        }

        // return captured piece
        return pawn;
    }

    /******************************* Debugging *******************************/

    String printBoard() {
        return chessBoard.toString();
    }
}