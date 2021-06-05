package chess;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.stage.Stage;
import javafx.stage.FileChooser;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.*;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

// visuals
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

// collections
import java.util.ArrayList;
import java.util.Optional;

// I/O
import java.io.File;

/**
 * JavaFX App
 */
public class App extends Application {

    final int squareSize = 50;
    final int small = 30;
    final int tiny = 25;
    final int numCanCapture = 5;
    final Font defaultFont = Font.font("Serif", FontWeight.NORMAL, 12);
    final Font checkFont = Font.font("Serif", FontWeight.NORMAL, 14);
    final Font titleFont = Font.font("Serif", FontWeight.NORMAL, 20);
    final String idleStyle = "-fx-border-color: transparent; -fx-background-color: transparent";
    final String hoverStyle = "-fx-border-color: red; -fx-background-color: transparent";
    final int buttonWidth = small * numCanCapture - 2 * tiny;

    ArrayList<Position> highlightedPos = new ArrayList<Position>();

    Game game;
    Scene scene;
    boolean live; // false = Read game from logs

    private void makeResign() {
        game.indicateResign();
        var resignBtn = (Button) scene.lookup("#resignBtn");
        resignBtn.setDisable(true); // cannot resign twice
        var l = (Label) scene.lookup("#checkedBox");
        l.setText("RESIGNED");
        l.setVisible(true);

        var text = game.turn.toString() + " has resigned.\nSave logs to "
                + System.getProperty("user.dir") + "/logs.txt?";
        var header = "Resigned";

        var alert = new Alert(Alert.AlertType.CONFIRMATION, text);
        alert.setHeaderText(header);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                game.saveLogs();
            }
            // disable the board
            var b = (TilePane) scene.lookup("#chessboard");
            b.setDisable(true);

            // prompt a restart
            var ctrl = (Button) scene.lookup("#restartBtn");
            ctrl.requestFocus();
        }
    }

    private void makeUncastle(Move move) {
        var kingMove = game.getUncastleKingMove();
        var oldKingPos = kingMove.old_pos;
        var newKingPos = kingMove.new_pos;

        var king = newKingPos.piece;
        var sp = (StackPane) scene.lookup("#" + oldKingPos.toString() + "_sp");
        var pc = (Button) sp.lookup("#" + king.toString());
        sp.getChildren().remove(pc);

        var spNew = (StackPane) scene
                .lookup("#" + newKingPos.toString() + "_sp");
        spNew.getChildren().add(pc);
    }

    private void makeUnpromote(Move move) {
        var pos = move.old_pos;
        var sp = (StackPane) scene.lookup("#" + pos.toString() + "_sp");
        var pawn = pos.piece;
        var pcPawn = makePieceButton(pawn);
        var queen = (Button) sp.getChildren().remove(1); // remove the queen
        sp.getChildren().add(pcPawn);

        var cap = (Label) scene.lookup("#Cap_" + queen.getId());
        cap.setText(String.valueOf(Integer.valueOf(cap.getText()) + 1));
        cap = (Label) scene.lookup("#Cap_" + pawn.toString());
        cap.setText(String.valueOf(Integer.valueOf(cap.getText()) - 1));
    }

    private void makeUncapture() {
        var capPiece = game.getCapturedPiece();
        var pos = capPiece.pos;
        var pc = makePieceButton(capPiece);
        var sp = (StackPane) scene.lookup("#" + pos.toString() + "_sp");
        sp.getChildren().add(pc);
        var cap = (Label) scene.lookup("#Cap_" + capPiece.toString());
        cap.setText(String.valueOf(Integer.valueOf(cap.getText()) - 1));
    }

    private void makeMove(Move move) {

        // complete current move
        var startPos = move.old_pos;
        var p = move.old_pos.piece;
        var sp = (StackPane) scene.lookup("#" + startPos.toString() + "_sp");
        var pc = (Button) sp.lookup("#" + p.toString());
        sp.getChildren().remove(pc);

        var endPos = move.new_pos;
        var spNew = (StackPane) scene.lookup("#" + endPos.toString() + "_sp");
        spNew.getChildren().add(pc);

        // account for captures
        var capturedPiece = game.makeMoveAndCapture(move);
        if (capturedPiece != null) {
            var pcCap = (Button) spNew.lookup("#" + capturedPiece.toString());
            spNew.getChildren().remove(pcCap);
            var cap = (Label) scene.lookup("#Cap_" + capturedPiece.toString());
            cap.setText(String.valueOf(Integer.valueOf(cap.getText()) + 1));
        }

        switch (move.action) {
        case CASTLE: {
            var rookMove = game.makeCastle(endPos);
            var startPosRook = rookMove.old_pos;
            var endPosRook = rookMove.new_pos;
            var rook = endPosRook.piece;
            var spRook = (StackPane) scene
                    .lookup("#" + startPosRook.toString() + "_sp");
            var pcRook = (Button) spRook.lookup("#" + rook.toString());
            spRook.getChildren().remove(pcRook);

            var spRookNew = (StackPane) scene
                    .lookup("#" + endPosRook.toString() + "_sp");
            spRookNew.getChildren().add(pcRook);
            break;
        }
        case PROMOTE: {
            var pawn = endPos.piece;
            var queen = game.makeNewQueen(endPos);
            var spPromote = (StackPane) scene
                    .lookup("#" + endPos.toString() + "_sp");
            var pcPawn = (Button) spPromote.lookup("#" + pawn.toString());
            spPromote.getChildren().remove(pcPawn);

            var cap = (Label) scene.lookup("#Cap_" + pawn.toString());
            cap.setText(String.valueOf(Integer.valueOf(cap.getText()) + 1));

            cap = (Label) scene.lookup("#Cap_" + queen.toString());
            cap.setText(String.valueOf(Integer.valueOf(cap.getText()) - 1));

            var pcQueen = makePieceButton(queen);
            spPromote.getChildren().add(pcQueen);
            break;
        }
        case ENPASSANT: {
            var capPawn = game.makeEnPassant(move);
            var spCap = (StackPane) scene
                    .lookup("#" + capPawn.pos.toString() + "_sp");
            var pcPawn = (Button) spCap.lookup("#" + capPawn.toString());
            spCap.getChildren().remove(pcPawn);

            var cap = (Label) scene.lookup("#Cap_" + capPawn.toString());
            cap.setText(String.valueOf(Integer.valueOf(cap.getText()) + 1));
            break;
        }
        default:
        }

        // account for checks
        var checked = game.isCheck();

        var checkedBox = (Label) scene.lookup("#checkedBox");
        // assuming the move is valid, the current side is not under check
        checkedBox.setVisible(false);
        // indicate if the opponent is now under check
        if (checked) {
            checkedBox.setVisible(true);
        }

        // disable all of current player's buttons
        unHighlight();
        deactivatePieces(game.turn);

        // switch players
        game.switchTurn();
        var title = (Label) scene.lookup("#titleText");
        title.setText(game.turn.toString() + " to Play.");
        title.requestFocus();

        // recalculate moves
        var hasMoves = game.getAllMoves();
        if (!hasMoves) {
            String text;
            String header;
            if (game.isCheckmated()) {
                // checkmate
                checkedBox.setText("CHECKMATE");
                game.indicateCheckmate();
                text = game.turn.toString()
                        + " has been checkmated.\nSave logs to "
                        + System.getProperty("user.dir") + "/logs.txt?";
                header = "Checkmate!";
            } else {
                // game.indicateStalemate();
                text = game.turn.toString()
                        + " has no more moves. Stalemate.\nSave logs to "
                        + System.getProperty("user.dir") + "/logs.txt?";
                header = "Stalemate!";
            }

            var resignBtn = (Button) scene.lookup("#resignBtn");
            resignBtn.setDisable(true); // cannot resign after game has ended

            var alert = new Alert(Alert.AlertType.CONFIRMATION, text);
            alert.setHeaderText(header);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    game.saveLogs();
                }
                // disable the board
                var b = (TilePane) scene.lookup("#chessboard");
                b.setDisable(true);

                // prompt a restart
                var ctrl = (Button) scene.lookup("#restartBtn");
                ctrl.requestFocus();
            }
        } else {
            // enable all of other player's buttons
            activatePieces(game.turn);
        }
    }

    private void makeUndo() {
        // disable all of current player's buttons
        unHighlight();
        deactivatePieces(game.turn);

        // if undoing from checkmate, need to reactivate the board
        if (game.isCheckmated()) {
            var l = (Label) scene.lookup("#checkedBox");
            l.setText("CHECKED");

            var b = (TilePane) scene.lookup("#chessboard");
            b.setDisable(false);
            b.requestFocus();
        }

        // the reversed undoing move
        var toUndo = game.undoMove();
        if (toUndo == null) {
            throw new NullPointerException("There is no move to undo!");
        }

        // additional actions, if any
        switch (toUndo.action) {
        case UNCASTLE:
            makeUncastle(toUndo);
            break;
        case UNPROMOTE:
            makeUnpromote(toUndo);
            break;
        case UNPROMOTEandUNCAPTURE:
            makeUnpromote(toUndo);
            makeUncapture();
            break;
        case UNCAPTURE:
            makeUncapture();
            break;
        default:
            break;
        }

        makeMove(toUndo);
        game.resetPieceStateUndo(toUndo.new_pos.piece);

        // to ensure that the player that undid his move gets to move
        // again next, make a no-op move for the other player
        game.switchTurnNoOp();
        makeMove(game.getNoOpKingMove());
        game.endNoOp();

        var resignBtn = (Button) scene.lookup("#resignBtn");
        if (game.turnNo >= 2) {
            resignBtn.setDisable(false);
        }

        // cannot undo further, and there's no point in restarting
        if (game.turn == Turn.WHITE && game.turnNo == 1) {
            var restartBtn = (Button) scene.lookup("#restartBtn");
            var undoBtn = (Button) scene.lookup("#undoBtn");
            resignBtn.setDisable(true);
            restartBtn.setDisable(true);
            undoBtn.setDisable(true);
        }
    }

    // pick a piece as the source move
    private void pcSrc(Piece p) {
        unHighlight();
        makeHighlighted(p);
    }

    private void makeHighlighted(Piece p) {
        var moves = p.getAvailableMoves();
        for (var move : moves) {
            var pos = move.new_pos;
            var sp = (StackPane) scene.lookup("#" + pos.toString() + "_sp");
            highlightedPos.add(pos);
            var sq = (Rectangle) sp.lookup("#" + pos.toString());
            sq.setFill(Color.DARKRED);

            var restartBtn = (Button) scene.lookup("#restartBtn");
            var undoBtn = (Button) scene.lookup("#undoBtn");
            var resignBtn = (Button) scene.lookup("#resignBtn");
            if (pos.piece != null) {
                // if occupied, enable the button
                var pc = (Button) sp.lookup("#" + pos.piece.toString());
                pc.setDisable(false);
                pc.setOnAction(e -> {
                    game.addToLogs(move);
                    makeMove(move);
                    if (game.turn == Turn.BLACK && game.turnNo == 1) {
                        restartBtn.setDisable(false);
                        undoBtn.setDisable(false);
                        resignBtn.setDisable(false);
                    }
                });
            } else {
                // if not occupied, add the pickdest event handler
                sq.setOnMouseClicked(e -> {
                    game.addToLogs(move);
                    makeMove(move);
                    if (game.turn == Turn.BLACK && game.turnNo == 1) {
                        restartBtn.setDisable(false);
                        undoBtn.setDisable(false);
                        resignBtn.setDisable(false);
                    }
                });
            }
        }
    }

    // unhighlight squares and disable any attached buttons
    private void unHighlight() {
        for (var pos : highlightedPos) {
            var sp = (StackPane) scene.lookup("#" + pos.toString() + "_sp");
            var sq = (Rectangle) sp.lookup("#" + pos.toString());
            sq.setFill(pos.isBlack ? Color.BLACK : Color.WHITE);

            if (pos.piece != null) {
                // disable and restore original handler
                var p = pos.piece;
                var pc = (Button) sp.lookup("#" + p.toString());
                pc.setDisable(true);
                pc.setOnAction(e -> pcSrc(p));
            }

            // remove the rectangle's handler
            sq.setOnMouseClicked(null);
        }

        // reset the list
        highlightedPos = new ArrayList<Position>();
    }

    private void activatePieces(Turn turn) {
        var pieces = game.ownPieces;
        for (var p : pieces) {
            var sp = (StackPane) scene.lookup("#" + p.pos.toString() + "_sp");
            var pc = (Button) sp.lookup("#" + p.toString());
            pc.setDisable(false);
            pc.setOnMouseEntered(e -> pc.setStyle(hoverStyle));
            pc.setOnMouseExited(e -> pc.setStyle(idleStyle));
        }
    }

    private void deactivatePieces(Turn turn) {
        var pieces = game.ownPieces;
        for (var p : pieces) {
            var sp = (StackPane) scene.lookup("#" + p.pos.toString() + "_sp");
            var pc = (Button) sp.lookup("#" + p.toString());
            pc.setDisable(true);
            pc.setOnMouseEntered(null);
            pc.setOnMouseExited(null);
        }
    }

    private GridPane makeCaptured(boolean isBlack) {
        var grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        var suff = isBlack ? "_black" : "_white";
        var Queen = new ImageView(new Image(
                getClass().getResourceAsStream("/icons/QUEEN" + suff + ".png"),
                small, small, false, false));
        var Rook = new ImageView(new Image(
                getClass().getResourceAsStream("/icons/ROOK" + suff + ".png"),
                small, small, false, false));
        var Knight = new ImageView(new Image(
                getClass().getResourceAsStream("/icons/KNIGHT" + suff + ".png"),
                small, small, false, false));
        var Bishop = new ImageView(new Image(
                getClass().getResourceAsStream("/icons/BISHOP" + suff + ".png"),
                small, small, false, false));
        var Pawn = new ImageView(new Image(
                getClass().getResourceAsStream("/icons/PAWN" + suff + ".png"),
                small, small, false, false));
        var QueenCap = new Label("0");
        QueenCap.setId("Cap_QUEEN" + suff);
        var RookCap = new Label("0");
        RookCap.setId("Cap_ROOK" + suff);
        var KnightCap = new Label("0");
        KnightCap.setId("Cap_KNIGHT" + suff);
        var BishopCap = new Label("0");
        BishopCap.setId("Cap_BISHOP" + suff);
        var PawnCap = new Label("0");
        PawnCap.setId("Cap_PAWN" + suff);
        grid.add(Queen, 0, 0);
        grid.add(QueenCap, 0, 1);
        grid.add(Rook, 1, 0);
        grid.add(RookCap, 1, 1);
        grid.add(Knight, 2, 0);
        grid.add(KnightCap, 2, 1);
        grid.add(Bishop, 3, 0);
        grid.add(BishopCap, 3, 1);
        grid.add(Pawn, 4, 0);
        grid.add(PawnCap, 4, 1);
        for (int i = 0; i < 5; ++i) {
            var cc = new ColumnConstraints();
            cc.setHalignment(HPos.CENTER);
            grid.getColumnConstraints().add(cc);
        }
        return grid;
    }

    private Button makePieceButton(Piece piece) {
        var pc = new Button();
        pc.setMaxHeight(squareSize);
        pc.setMaxWidth(squareSize);
        pc.setStyle(idleStyle);
        pc.setDisable(true); // all pieces disabled before game starts
        String path = "/icons/" + piece.toString() + ".png";
        Image img = new Image(getClass().getResourceAsStream(path),
                squareSize * 0.95, squareSize * 0.95, false, false);
        pc.setGraphic(new ImageView(img));
        pc.setPadding(Insets.EMPTY);
        StackPane.setMargin(pc, Insets.EMPTY);

        // add event handler
        pc.setOnAction(e -> pcSrc(piece));
        pc.setId(piece.toString());
        return pc;
    }

    private StackPane makeSquare(Position pos) {
        var isBlack = pos.isBlack;
        var col = isBlack ? Color.BLACK : Color.WHITE;
        var sq = new Rectangle(squareSize, squareSize, col);
        sq.setStyle(
                "-fx-stroke: black; -fx-stroke-type: inside; -fx-stroke-width: 0.1;");
        StackPane.setMargin(sq, Insets.EMPTY);
        sq.setId(pos.toString());

        var sp = new StackPane(sq);
        sp.setId(pos.toString() + "_sp"); // uniquely identify the stack pane
        sp.setMaxSize(squareSize, squareSize);
        var piece = pos.piece;
        if (piece != null) {
            Button pieceCtrl = makePieceButton(piece);
            sp.getChildren().add(pieceCtrl);
        }
        return sp;
    }

    private void showErrorDialog(Thread t, Throwable e) {
        e.printStackTrace();
        var text = "A Fatal Error has occurred in the App.\nPlease restart the app.";
        var alert = new Alert(Alert.AlertType.ERROR, text);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
            // // disable the board
            // var b = (TilePane) scene.lookup("#chessboard");
            // b.setDisable(true);

            // // prompt a restart
            // var ctrl = (Button) scene.lookup("#restartBtn");
            // ctrl.requestFocus();
        }
    }

    private boolean readFile(Stage stage) {
        var fc = new FileChooser();
        fc.setTitle("Choose Log File");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(stage);
        return game.readFile(f);
    }

    private void initUI(Stage stage, boolean shouldStartGame, boolean l) {
        System.out.println(l ? "LIVE Mode" : "READ Mode");
        game = new Game();
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> Platform.runLater(() -> showErrorDialog(t, e)));
        // https://stackoverflow.com/questions/25145956/how-can-i-create-a-default-exception-handler-that-uses-javafx

        live = l;
        // bottom
        var javaVersion = System.getProperty("java.version");
        var javafxVersion = System.getProperty("javafx.version");
        var info = new Label("App is running on JavaFX " + javafxVersion
                + " and Java " + javaVersion + ".");
        info.setFont(defaultFont);

        var link1 = new Hyperlink("Icons licensed by icons8");
        link1.setFont(defaultFont);
        link1.setPadding(new Insets(0));
        link1.setStyle(" -fx-text-fill:steelblue; -fx-underline: false");
        link1.setOnAction(e -> {
            getHostServices().showDocument("https://icons8.com");
        });
        var divider = new Label("|");
        var link2 = new Hyperlink("View the source code");
        link2.setFont(defaultFont);
        divider.setFont(defaultFont);
        link2.setPadding(new Insets(0));
        link2.setStyle(" -fx-text-fill:steelblue; -fx-underline: false");
        link2.setOnAction(e -> {
            getHostServices().showDocument("https://github.com/kxiao1/Chess2D");
        });
        var links = new HBox(3, link1, divider, link2);
        links.setAlignment(Pos.TOP_CENTER);

        var footer = new VBox(0, info, links);
        footer.setPadding(new Insets(20, 0, 10, 0));
        footer.setAlignment(Pos.CENTER);

        // top: toolbar
        var title = new Label(
                "“There is no remorse like the remorse of chess.”");
        title.setFont(titleFont);
        title.setId("titleText");

        var checkedBox = new Label("CHECKED");
        checkedBox.setFont(checkFont);
        checkedBox.setTextFill(Color.RED);
        checkedBox.setId("checkedBox");
        checkedBox.setVisible(false); // by default this text should be
                                      // invisible

        var resignBtn = new Button("Resign");
        resignBtn.setOnAction(e -> makeResign());
        resignBtn.setId("resignBtn");
        resignBtn.setDisable(true); // cannot resign before starting a game

        var Toolbar = new VBox(5, title, resignBtn, checkedBox);
        Toolbar.setPadding(new Insets(30, 5, 0, 5));
        Toolbar.setAlignment(Pos.BOTTOM_CENTER);

        // left: gameplay buttons
        var g = new ToggleGroup();
        var tLive = new RadioButton("LIVE");
        tLive.setToggleGroup(g);
        tLive.setSelected(live);
        tLive.setMaxWidth(0.5 * small * numCanCapture);
        tLive.setUserData(true);

        var tRead = new RadioButton("READ");
        tRead.setToggleGroup(g);
        tRead.setSelected(!live);
        tRead.setMaxWidth(0.5 * small * numCanCapture);
        tRead.setUserData(false);

        g.selectedToggleProperty().addListener((ov, o, n) -> {
            initUI(stage, false, (boolean) n.getUserData());
        });
        var toggles = new HBox(5, tLive, tRead);
        toggles.setAlignment(Pos.CENTER);

        var startBtn = new Button("Start");
        if (live) {
            startBtn.setOnAction(e -> {
                var t = (Label) Toolbar.lookup("#titleText");
                t.setText(game.turn + "'s Turn");
                startBtn.setDisable(true);
                game.getAllMoves();
                activatePieces(game.turn);
                title.requestFocus();
            });
        } else {
            startBtn.setOnAction(e -> {
                var t = (Label) Toolbar.lookup("#titleText");
                t.setText(game.turn + "'s Turn");
                if (readFile(stage)) { // if successful
                    startBtn.setDisable(true);
                    // game.getAllMoves(); # do not allow manual moves!
                    // activatePieces(game.turn);
                    title.requestFocus();

                } // else allow user to select a new file
            });
        }
        startBtn.setMaxWidth(buttonWidth);

        var undoBtn = new Button(live ? "Undo" : "Prev");
        undoBtn.setId("undoBtn");
        undoBtn.setOnAction(e -> makeUndo());
        undoBtn.setDisable(true);
        undoBtn.setMaxWidth(buttonWidth);

        var restartBtn = new Button("Restart");
        restartBtn.setId("restartBtn");
        restartBtn.setOnAction(e -> {
            initUI(stage, true, live);
        });
        restartBtn.setDisable(true);
        restartBtn.setMaxWidth(buttonWidth);

        var saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            var text = "Save logs to " + System.getProperty("user.dir")
                    + "/logs.txt?\n" + "This will overwrite any existing file.";
            var alert = new Alert(Alert.AlertType.CONFIRMATION, text);
            alert.setHeaderText("Saving logs");
            var result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                game.saveLogs();
            }
        });
        saveBtn.setMaxWidth(buttonWidth);

        var redoBtn = new Button(live ? "Redo" : "Next");
        redoBtn.setOnAction(e -> {
            System.out.println("To be Implemented");
        });
        redoBtn.setMaxWidth(buttonWidth);

        var quitBtn = new Button("Quit");
        quitBtn.setOnAction(e -> {
            var text = "Remember to save before quitting!";
            var alert = new Alert(Alert.AlertType.CONFIRMATION, text);
            alert.setHeaderText("You are closing the app.");
            var result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Platform.exit();
            }
        });
        quitBtn.setMaxWidth(buttonWidth);

        VBox ctrls = new VBox(10, toggles, startBtn, undoBtn, redoBtn,
                restartBtn, saveBtn, quitBtn);
        ctrls.setPadding(new Insets(10));
        ctrls.setMinWidth(small * numCanCapture + 18); // best effort at
                                                       // aligning
        ctrls.setAlignment(Pos.CENTER);

        // right: captured pieces
        var rightLabel = new Label("Captured:");
        rightLabel.setFont(titleFont);
        var blackCaptured = makeCaptured(true);
        var whiteCaptured = makeCaptured(false);
        var Captured = new VBox(20, rightLabel, blackCaptured, whiteCaptured);
        Captured.setPadding(new Insets(10));
        Captured.setMinWidth(small * numCanCapture);
        Captured.setAlignment(Pos.CENTER);

        // center: chessboard
        TilePane tile = new TilePane();
        tile.setPrefColumns(Board.NumX);
        tile.setId("chessboard");
        var tChild = tile.getChildren();
        tile.setMaxWidth((Board.NumX + 2) * squareSize);
        tile.setMaxHeight((Board.NumY + 2) * squareSize);
        tile.setStyle("-fx-border-color: black");

        var squares = game.getSquares();
        for (int y = Board.NumY - 1; y >= 0; --y) {
            for (int x = 0; x < Board.NumX; ++x) {
                Position pos = squares[x][y];
                var sp = makeSquare(pos);
                tChild.add(sp);
            }
        }

        var centerGrid = new GridPane();
        centerGrid.setStyle("-fx-background-color: white");
        centerGrid.setMaxSize(Board.NumX * squareSize + 2 * tiny + 2,
                Board.NumY * squareSize + 2 * tiny + 2);
        centerGrid.setAlignment(Pos.CENTER);

        // it suffices to set only the top left and bottom right dummies
        var dummy = new StackPane();
        dummy.setMinSize(tiny, tiny);
        centerGrid.add(dummy, 0, 0, 1, 1);
        for (int i = 1; i < Board.NumY + 1; ++i) {
            var text = new Label(String.valueOf(Board.NumY + 1 - i));
            text.setFont(titleFont);
            var lab = new StackPane(text);
            lab.setMinSize(tiny, squareSize);
            centerGrid.add(lab, 0, i, 1, 1);
        }

        centerGrid.add(tile, 1, 1, Board.NumX, Board.NumY);

        for (int i = 1; i < Board.NumY + 1; ++i) {
            var text = new Label(String.valueOf((char) (i + 96)));
            text.setFont(titleFont);
            var lab = new StackPane(text);
            lab.setMinSize(squareSize, tiny);
            centerGrid.add(lab, i, Board.NumY + 1, 1, 1);
        }
        dummy = new StackPane();
        dummy.setMinSize(tiny, tiny);
        centerGrid.add(dummy, Board.NumX + 1, Board.NumY + 1, 1, 1);

        // set BorderPane counterclockwise
        BorderPane border = new BorderPane();
        border.setTop(Toolbar);
        border.setLeft(ctrls);
        border.setBottom(footer);
        border.setRight(Captured);
        border.setCenter(centerGrid);

        scene = new Scene(border, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Chess2D");
        stage.setResizable(false);
        stage.show();

        if (shouldStartGame) {
            startBtn.fire();
        }
    }

    @Override
    public void start(Stage stage) {
        initUI(stage, false, true);
    }

    public static void main(String[] args) {
        launch();
    }

}