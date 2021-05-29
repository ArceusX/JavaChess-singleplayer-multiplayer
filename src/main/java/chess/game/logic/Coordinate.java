package chess.game.logic;

import java.io.Serializable;

public class Coordinate implements Serializable {
    public int row, col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    Colour getColour() {
        /* Because board is checkered, easy formula to calculate colour.
           i.e. (0, 0) Cell is White, and a shift one row or col results in a Black Cell. */
        return ((row+col) % 2 == 0) ? Colour.WHITE : Colour.BLACK;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        Coordinate itsCoordinate = (Coordinate) obj;
        return this.row == itsCoordinate.row && this.col == itsCoordinate.col;
    }
}
