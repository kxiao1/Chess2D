# 2D Chess App

Built with JavaFX and Maven over Winter Break 2020-21.

## Progress

- [x] Board Layout
- [x] Graphics for Chess Pieces
- [x] Highlighting Possible Moves
- [x] Piece Logic
- [x] Check for Checks
- [x] Captures
- [x] Promoting
- [x] Castling
- [x] En Passant
- [x] Algebraic Notation and Outputing Logs
- [x] Restarts
- [x] Undos
- [x] Resigning

## Possible Extensions

- Playing with other humans over sockets (`java.net`). In this remote multiplayer mode (which requires a new UI), the player wishing to be White creates a client `Socket` and Black starts a `ServerSocket`. Each process will have two threads, one playing moves locally and the other relaying opponent moves.
- ~~Replaying games from logs. Need to implement move-parsing and create a new UI; some fast-forwarding functionality would be desirable here.~~ (<i>In Progress Summer 2021!</i>)
- Installing a customizable timer. The countdown display can be controlled with a property binding (e.g. `Label.textProperty().bind(TIMELEFT)`, thanks to `javafx.beans.property.IntegerProperty`). The numerical value can be updated with a `Timeline` and `KeyFrame` from `javafx.animation`.

## Usage

After cloning the repository, ensure that `JDK`, `JRE`, and Maven are available (there's no need to explicitly install JavaFX). Then run the following command to test the app locally:

`mvn clean javafx:run`

To build a runtime image, run the above command and then `mvn javafx:jlink`. Run the executable image with `target/image/bin/java -m kxiao1/chess.App` (the `image` folder is portable).

It is possible to build a `.jar` package with `mvn package`. However, running it using the command `java` requires the [JavaFX runtime](https://gluonhq.com/products/javafx/) to be downloaded and made known to the JRE. This is because JDK 11+ removed built-in support for JavaFX. See https://openjfx.io/openjfx-docs/#install-javafx for possible solutions (this has not been tested).

## Gameplay

- Click a piece to see its legal moves highlighted, then click one of the highlighted squares to move it.
- To castle, move the King and not the Rook.
- En Passant captures are automatically performed and promotions are always to Queens.
- Undo's and restarts are permitted starting from the first Black move.
- The outputed logs follow Portable Game Notation (PGN) but are intentionally verbose: the starting position is always specified. This prevents any ambiguity from the outset.

## Known issues

- Linux users: To open the URL for the source code, make sure `xdg-open` is installed (e.g. `sudo apt install xdg-utils`).
