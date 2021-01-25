# 2D Chess App
Built with JavaFX and Maven.

## Progress
- [x] Board Layout
- [x] Graphics for Chess Pieces
- [x] Highlighting Possible Moves
- [x] Piece Logic
- [x] Check for Checks
- [x] Captures 
- [ ] Promoting
- [x] Castling 
- [ ] En Passant
- [x] Algebraic Notation and Outputing Logs
- [ ] Timer
- [x] Restarts
- [x] Undos
- [ ] Playing Games from Logs

## Usage
After cloning the repository, ensure that ``JDK``, ``JRE``, and Maven are available (there's no need to explicitly install JavaFX). Then run the following command to test the app locally:

``mvn clean javafx:run``

To build a runtime image, run the above command and then ``mvn javafx:jlink``. Run the executable image with ``target/image/bin/java -m kxiao1/chess.App`` (the image folder is portable).

## Gameplay
- Click a piece to see its legal moves highlighted, then click one of the highlighted squares to move it.
- To castle, move the King and not the Rook. 
- (TODO) En Passants are automatically performed and promotions are always to Queens.
- The outputed logs follow Portable Game Notation (PGN) but are intentionally verbose: the starting position is always specified. This prevents any ambiguity from the outset.

## Known issues
- Linux users: To open the URL for the source code, make sure ``xdg-open`` is installed (e.g.``sudo apt install xdg-utils``).

