package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.io.Serializable;
import java.util.ArrayList;

import static chess.game.logic.Board.isEmpty;
import static chess.game.logic.Board.isKingAttackedIfPieceRemoved;
import static chess.game.logic.Board.sameColourPiece;

public class Rook extends Piece implements Serializable {

    public Rook(Colour colour) {
        name = ChessPiece.ROOK;

        if (colour == Colour.WHITE) {
            image = createImageIcon("img/WhiteRook.png");
            this.colour = Colour.WHITE;

        }
        else {
            image = createImageIcon("img/BlackRook.png");
            this.colour = Colour.BLACK;
        }
        isInPlay = true;
    }

    @Override
    public ArrayList<Coordinate> getLegalMoves(Cell cell) {

        ArrayList<Coordinate> legalToCoordinates = new ArrayList<Coordinate>();
        int cellrow = cell.getRow();
        int cellcol = cell.getCol();

        int rowptr;
        int colptr;

        colptr = cellcol - 1;

        while (colptr >= 0) {

            if (isEmpty(cellrow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellrow, colptr));
                colptr--;
            } else if (!sameColourPiece(cell, cellrow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellrow, colptr));
                break;
            } else
                break;
        }
        colptr = cellcol + 1;
        while (colptr <= 7) {
            if (isEmpty(cellrow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellrow, colptr));
                colptr++;
            } else if (!sameColourPiece(cell, cellrow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellrow, colptr));
                break;
            } else
                break;
        }

        rowptr = cellrow + 1;
        while (rowptr <= 7) {
            if (isEmpty(rowptr, cellcol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellcol));
                rowptr++;
            } else if (!sameColourPiece(cell, rowptr, cellcol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellcol));
                break;
            } else
                break;
        }

        rowptr = cellrow - 1;
        while (rowptr >= 0) {
            if (isEmpty(rowptr, cellcol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellcol));
                rowptr--;
            } else if (!sameColourPiece(cell, rowptr, cellcol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellcol));
                break;
            } else
                break;
        }

        ArrayList<Coordinate> pinnedcoordinates = isKingAttackedIfPieceRemoved(cell);

        if(pinnedcoordinates != null) {

            return intersection(pinnedcoordinates,legalToCoordinates);
        }

        return legalToCoordinates;
    }
}
