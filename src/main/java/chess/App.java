package chess;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.scene.Scene;
import javafx.stage.Stage;
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
import javafx.event.ActionEvent;

// visuals
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

// collections
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {

    final int squareSize = 50;
    final Font defaultFont = Font.font("Serif", FontWeight.NORMAL, 12);
    final Font titleFont = Font.font("Serif", FontWeight.NORMAL, 20);
    final String idleStyle = "-fx-border-color: transparent; -fx-background-color: transparent";
    final String hoverStyle = "-fx-border-color: red; -fx-background-color: transparent";

    ArrayList<Position> highlightedPos = new ArrayList<Position>();

    Game game;
    Scene scene;

    private void makeMove(Move move) {

        // complete current move
        var startPos = move.old_pos;
        var p = move.old_pos.piece;
        var sp = (StackPane) scene.lookup("#" + startPos.toString() + "_sp");
        var pc = (Button) sp.lookup("#" + p.toString());
        sp.getChildren().remove(pc);

        var pcNew = makePieceButton(p);
        var endPos = move.new_pos;
        var spNew = (StackPane) scene.lookup("#" + endPos.toString() + "_sp");
        spNew.getChildren().add(pcNew);

        // (TODO: account for check etc)
        var capturedPiece = game.makeMoveAndCapture(move);
        if (capturedPiece != null) {
            var pcCap = (Button) spNew.lookup("#" + capturedPiece.toString());
            spNew.getChildren().remove(pcCap);
            var cap = (Label) scene.lookup("#Cap_" + capturedPiece.toString());
            var newChar = cap.getText().charAt(0);
            newChar++;
            cap.setText(String.valueOf(newChar));
        }

        // disable all of current player's buttons
        unHighlight();
        deactivatePieces(game.turn);

        // switch players
        game.switchTurn();
        var title = (Label) scene.lookup("#titleText");
        title.setText(game.turn.toString() + " to play.");
        title.requestFocus();

        // recalculate moves
        game.getAllMoves();

        // enable all of other player's buttons
        activatePieces(game.turn);
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

            if (pos.piece != null) {
                // if occupied, enable the button
                var pc = (Button) sp.lookup("#" + pos.piece.toString());
                pc.setDisable(false);
                pc.setOnAction(e -> makeMove(move));
            } else {
                // if not occupied, add the pickdest event handler
                sq.setOnMouseClicked(e -> makeMove(move));
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
                var pc = (Button) sp.lookup("#" + pos.piece.toString());
                pc.setDisable(true);
                pc.setOnAction(e -> pcSrc(pos.piece));
            } else {
                // remove the rectangle's handler
                sq.setOnMouseClicked(null);
            }
        }

        // reset the list
        highlightedPos = new ArrayList<Position>();
    }

    private void activatePieces(Turn turn) {
        var pieces = (turn == Turn.BLACK ? game.blackPieces : game.whitePieces);
        for (var p : pieces) {
            var sp = (StackPane) scene.lookup("#" + p.pos.toString() + "_sp");
            var pc = (Button) sp.lookup("#" + p.toString());
            pc.setDisable(false);
            pc.setOnMouseEntered(e -> pc.setStyle(hoverStyle));
            pc.setOnMouseExited(e -> pc.setStyle(idleStyle));
        }
    }

    private void deactivatePieces(Turn turn) {
        var pieces = (turn == Turn.BLACK ? game.blackPieces : game.whitePieces);
        for (var p : pieces) {
            var sp = (StackPane) scene.lookup("#" + p.pos.toString() + "_sp");
            var pc = (Button) sp.lookup("#" + p.toString());
            pc.setDisable(true);
            pc.setOnMouseEntered(null);
            pc.setOnMouseExited(null);
        }
    }

    private GridPane makeCaptured(boolean isBlack) {
        int small = 30;
        var grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        var suff = isBlack ? "_black" : "_white";
        var Queen = new ImageView(
                new Image(getClass().getResourceAsStream("/icons/QUEEN" + suff + ".png"), small, small, false, false));
        var Rook = new ImageView(
                new Image(getClass().getResourceAsStream("/icons/ROOK" + suff + ".png"), small, small, false, false));
        var Knight = new ImageView(
                new Image(getClass().getResourceAsStream("/icons/KNIGHT" + suff + ".png"), small, small, false, false));
        var Bishop = new ImageView(
                new Image(getClass().getResourceAsStream("/icons/BISHOP" + suff + ".png"), small, small, false, false));
        var Pawn = new ImageView(
                new Image(getClass().getResourceAsStream("/icons/PAWN" + suff + ".png"), small, small, false, false));
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
        Image img = new Image(getClass().getResourceAsStream(path), squareSize * 0.95, squareSize * 0.95, false, false);
        pc.setGraphic(new ImageView(img));
        pc.setPadding(Insets.EMPTY);
        StackPane.setMargin(pc, Insets.EMPTY);

        // add event handler
        pc.setOnAction(e -> pcSrc(piece));
        pc.setId(piece.toString());
        return pc;
    }

    private void initUI(Stage stage) {

        game = new Game();

        // bottom
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        var info = new Label("App developed with JavaFX " + javafxVersion + " and Java " + javaVersion + ".");
        info.setFont(defaultFont);
        var link = new Hyperlink("View the source code.");
        link.setFont(defaultFont);
        link.setOnAction(e -> {
            getHostServices().showDocument("https://github.com/kxiao1/Chess2D");
        });
        HBox footer = new HBox(5, info, link);
        footer.setAlignment(Pos.CENTER);

        // top: toolbar
        var Toolbar = new HBox(10);
        Toolbar.setPadding(new Insets(5));
        var title = new Label("Press 'Start' for a New Game.");
        title.setFont(titleFont);
        title.setId("titleText");
        Toolbar.getChildren().add(title);
        Toolbar.setAlignment(Pos.CENTER);

        // left: gameplay buttons
        var startBtn = new Button("Start");
        startBtn.setOnAction(e -> {
            var t = (Label) Toolbar.lookup("#titleText");
            t.setText(game.turn + " to play.");
            startBtn.setDisable(true);
            game.getAllMoves();
            activatePieces(game.turn);
            title.requestFocus();
        });

        var restartBtn = new Button("Restart");
        restartBtn.setOnAction(e -> {
            initUI(stage);
        });
        var saveBtn = new Button("Save (TODO)");

        var quitBtn = new Button("Quit");
        quitBtn.setOnAction(e -> {
            Platform.exit();
        });

        VBox ctrls = new VBox(10, startBtn, restartBtn, saveBtn, quitBtn);
        ctrls.setPadding(new Insets(5));
        ctrls.setAlignment(Pos.CENTER_LEFT);

        // right: captured pieces
        var rightLabel = new Label("Captured Pieces");
        rightLabel.setFont(titleFont);
        rightLabel.setPadding(new Insets(5));
        var blackCaptured = makeCaptured(true);
        var whiteCaptured = makeCaptured(false);
        var Captured = new VBox(20, rightLabel, blackCaptured, whiteCaptured);
        Captured.setAlignment(Pos.CENTER);

        // center: chessboard
        TilePane tile = new TilePane();
        tile.setPrefColumns(Board.NumX);
        var tChild = tile.getChildren();
        tile.setMaxWidth(Board.NumX * squareSize);
        tile.setMaxHeight(Board.NumY * squareSize);
        var squares = game.chessBoard.getSquares();
        for (int y = Board.NumY - 1; y >= 0; --y) {
            for (int x = 0; x < Board.NumX; ++x) {
                Position pos = squares[x][y];
                var isBlack = pos.isBlack;
                var col = isBlack ? Color.rgb(0, 0, 0) : Color.rgb(255, 255, 255);
                var sq = new Rectangle(squareSize, squareSize, col);
                StackPane.setMargin(sq, Insets.EMPTY);
                sq.setId(pos.toString());

                var sp = new StackPane(sq);
                sp.setId(pos.toString() + "_sp"); // uniquely identify the stack pange
                sp.setMaxSize(squareSize, squareSize);
                var piece = pos.piece;
                if (piece != null) {
                    Button pieceCtrl = makePieceButton(piece);
                    sp.getChildren().add(pieceCtrl);
                }

                tChild.add(sp);
            }
        }

        // set BorderPane counterclockwise
        BorderPane border = new BorderPane();
        border.setTop(Toolbar);
        border.setLeft(ctrls);
        border.setBottom(footer);
        border.setRight(Captured);
        border.setCenter(tile);

        scene = new Scene(border, 800, 600);
        title.requestFocus();
        stage.setScene(scene);
        stage.setTitle("Chess2D");
        stage.show();
    }

    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    public static void main(String[] args) {
        launch();
    }

}