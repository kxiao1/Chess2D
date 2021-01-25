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

## Use
After cloning the repository, ensure that ``JDK``, ``JRE``, and Maven are available (there's no need to explicitly install JavaFX). Then run the following command to test the app locally:

``mvn clean javafx:run``

To create a ``.jar`` file, use ``mvn package``.

- To castle, move the King and not the Rook. 
- The logs follow Portable Game Notation (PGN) but are intentionally verbose: the starting position is always specified. This prevents any ambiguity from the outset.

## Known issues
- Linux users: make sure ``xdg-open`` is installed (e.g.``sudo apt install xdg-utils``).

