package chess;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.HostServices;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;


import javafx.scene.control.Hyperlink;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

// visuals
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        Game game = new Game();

        var defaultFont = Font.font("Serif", FontWeight.NORMAL, 12);
        var titleFont = Font.font("Serif", FontWeight.NORMAL, 20);
        
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

        // left
        var startBtn = new Button();
        startBtn.setText("Start");
        var quitBtn = new Button();
        quitBtn.setText("Quit");
        quitBtn.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });
        VBox ctrls = new VBox(10, startBtn, quitBtn);

        // right: captured pieces TODO
        var Captured = new VBox();
        var rightLabel = new Label("Captured\nPieces");
        rightLabel.setFont(titleFont);
        Captured.getChildren().add(rightLabel);

        // top: toolbar
        var Toolbar = new HBox(5);
        var title = new Label("2D Chess");
        title.setFont(titleFont);
        Toolbar.getChildren().add(title);
        Toolbar.setAlignment(Pos.CENTER);

        // center: chessboard
        TilePane tile = new TilePane();
        var tChild = tile.getChildren();
        tile.setMaxWidth(Board.NumX * 50);
        tile.setMaxHeight(Board.NumY * 50);
        for (int y =  Board.NumY - 1; y >= 0; --y) {
            for (int x = 0; x < Board.NumX; ++x) {
                Position pos = game.squares[x][y]; 
                var isBlack = pos.isBlack;
                var col = isBlack ? Color.rgb(0,0,0) : Color.rgb(255, 255, 255);
                var Sq = new Rectangle(50, 50, col);
                Sq.setId(pos.toString());
                tChild.add(Sq);
            }
        }

        // set BorderPane counterclockwise
        BorderPane border = new BorderPane();
        border.setTop(Toolbar);
        border.setLeft(ctrls);
        border.setBottom(footer);
        border.setRight(Captured);
        border.setCenter(tile);

        var scene = new Scene(border, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Chess2D");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}