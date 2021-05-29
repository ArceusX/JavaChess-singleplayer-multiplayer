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

    public Rook(Colour c) {

        name = ChessPiece.ROOK;
        if(c == Colour.BLACK) {
            image = createImageIcon("img/BlackRook.png");
            colour = Colour.BLACK;
            isInPlay = true;
        }
        else {
            image = createImageIcon("img/WhiteRook.png");
            colour = Colour.WHITE;
            isInPlay = true;
        }
    }

    @Override
    public ArrayList<Coordinate> legalMoves(Cell c) {

        ArrayList<Coordinate> legalToCoordinates = new ArrayList<Coordinate>();
        int cellrow = c.getRow();
        int cellcol = c.getCol();

        int rowptr;
        int colptr;

        colptr = cellcol - 1;

        while (colptr >= 0) {

            if (isEmpty(cellrow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellrow, colptr));
                colptr--;
            } else if (!sameColourPiece(c, cellrow, colptr)) {
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
            } else if (!sameColourPiece(c, cellrow, colptr)) {
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
            } else if (!sameColourPiece(c, rowptr, cellcol)) {
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
            } else if (!sameColourPiece(c, rowptr, cellcol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellcol));
                break;
            } else
                break;
        }

        ArrayList<Coordinate> pinnedcoordinates = isKingAttackedIfPieceRemoved(c);

        if(pinnedcoordinates != null) {

            return intersection(pinnedcoordinates,legalToCoordinates);
            //fill this
        }

        return legalToCoordinates;
    }
}
