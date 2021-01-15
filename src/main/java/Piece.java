package chess.src.main.java;
import java.util.ArrayList;

public abstract class Piece {
    protected Position pos;
    protected boolean hasMoved;

    public Piece(Position pos) {
        this.pos = pos;
        hasMoved = false;
    }
    public abstract ArrayList<Move> getAvailableMoves();


}