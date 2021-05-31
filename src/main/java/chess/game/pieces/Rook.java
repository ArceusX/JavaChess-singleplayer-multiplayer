package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static chess.game.logic.Board.isEmpty;
import static chess.game.logic.Board.isKingAttackedIfPieceRemoved;
import static chess.game.logic.Board.isMatchedColour;

public class Rook extends Piece implements Serializable {

    public Rook(Colour colour) {
        super(colour);

        name = ChessPiece.ROOK;
        image = (colour == Colour.WHITE) ?
                createImageIcon("img/WhiteRook.png") : createImageIcon("img/BlackRook.png");
    }

    @Override
    public List<Coordinate> getLegalMoves(Cell fromCell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();
        int row = fromCell.getRow();
        int col = fromCell.getCol();

        int rowPtr;
        int colPtr;

        colPtr = col - 1;

        while (colPtr >= 0) {

            if (isEmpty(row, colPtr)) {
                legalToCoordinates.add(new Coordinate(row, colPtr));
                colPtr--;
            } else if (!isMatchedColour(fromCell, row, colPtr)) {
                legalToCoordinates.add(new Coordinate(row, colPtr));
                break;
            } else
                break;
        }
        colPtr = col + 1;
        while (colPtr <= 7) {
            if (isEmpty(row, colPtr)) {
                legalToCoordinates.add(new Coordinate(row, colPtr));
                colPtr++;
            } else if (!isMatchedColour(fromCell, row, colPtr)) {
                legalToCoordinates.add(new Coordinate(row, colPtr));
                break;
            } else
                break;
        }

        rowPtr = row + 1;
        while (rowPtr <= 7) {
            if (isEmpty(rowPtr, col)) {
                legalToCoordinates.add(new Coordinate(rowPtr, col));
                rowPtr++;
            } else if (!isMatchedColour(fromCell, rowPtr, col)) {
                legalToCoordinates.add(new Coordinate(rowPtr, col));
                break;
            } else
                break;
        }

        rowPtr = row - 1;
        while (rowPtr >= 0) {
            if (isEmpty(rowPtr, col)) {
                legalToCoordinates.add(new Coordinate(rowPtr, col));
                rowPtr--;
            } else if (!isMatchedColour(fromCell, rowPtr, col)) {
                legalToCoordinates.add(new Coordinate(rowPtr, col));
                break;
            } else
                break;
        }

        List<Coordinate> pinnedCoordinates = isKingAttackedIfPieceRemoved(fromCell);

        if(pinnedCoordinates != null) {
            return intersection(pinnedCoordinates,legalToCoordinates);
        }

        return legalToCoordinates;
    }
}
