package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import static chess.game.logic.Board.isKingAttackedIfPieceRemoved;
import static chess.game.logic.Board.isMatchedColour;

public class Knight extends Piece implements Serializable {
    public Knight(Colour colour) {
        super(colour);

        name = ChessPiece.KNIGHT;
        image = (colour == Colour.WHITE) ?
                createImageIcon("img/WhiteKnight.png") : createImageIcon("img/BlackKnight.png");
    }

    @Override
    public List<Coordinate> getLegalMoves(Cell fromCell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();
        int row = fromCell.getRow();
        int col = fromCell.getCol();

        if (row - 1 >= 0) {
            //Check the cells to which the Knight can move that is one row above
            if (col - 2 >= 0 && !isMatchedColour(fromCell, row - 1, col - 2)) {
                legalToCoordinates.add(new Coordinate(row - 1, col - 2));
            }

            if (col + 2 < 8 && !isMatchedColour(fromCell,row - 1,col + 2)) {
                legalToCoordinates.add(new Coordinate(row-1,col+2));
            }

            if (row - 2 >= 0) {
                if (col - 1 >= 0 && !isMatchedColour(fromCell, row - 2, col - 1)) {
                    legalToCoordinates.add(new Coordinate(row - 2, col - 1));
                }

                if (col+1 <= 7 && !isMatchedColour(fromCell,row - 2,col + 1)) {
                    legalToCoordinates.add(new Coordinate(row - 2, col + 1));
                }
            }
        }

        if (row + 1 < 8) {
            if (col - 2 >= 0 && !isMatchedColour(fromCell,row + 1,col - 2)) {
                legalToCoordinates.add(new Coordinate(row + 1,col - 2));
            }

            if (col + 2 < 8 && !isMatchedColour(fromCell, row + 1, col + 2)) {
                legalToCoordinates.add(new Coordinate(row+1,col+2));
            }

            if (row + 2 < 8) {
                if (col - 1 >= 0 && !isMatchedColour(fromCell, row + 2, col - 1)) {
                    legalToCoordinates.add(new Coordinate(row + 2, col - 1));
                }

                if (col + 1 < 8 && !isMatchedColour(fromCell, row + 2, col + 1)) {
                    legalToCoordinates.add(new Coordinate(row + 2, col + 1));
                }
            }
        }
        List<Coordinate> pinnedCoordinates = isKingAttackedIfPieceRemoved(fromCell);

        if(pinnedCoordinates != null) {
            return intersection(pinnedCoordinates,legalToCoordinates);
        }

        return legalToCoordinates;
    }
}
