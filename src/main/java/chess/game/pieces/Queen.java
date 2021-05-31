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
import static chess.game.logic.Board.sameColourPiece;

public class Queen extends Piece implements Serializable {
    public Queen(Colour colour) {

        name = ChessPiece.QUEEN;
        if (colour == Colour.WHITE) {
            image = createImageIcon("img/WhiteQueen.png");
            this.colour = Colour.WHITE;

        }
        else {
            image = createImageIcon("img/BlackQueen.png");
            this.colour = Colour.BLACK;
        }
        isInPlay = true;
    }

    @Override
    public List<Coordinate> getLegalMoves(Cell cell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();
        int cellRow = cell.getRow();
        int cellCol = cell.getCol();

        int rowptr;
        int colptr;

        colptr = cellCol - 1;

        while (colptr >= 0) {

            if (isEmpty(cellRow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellRow, colptr));
                colptr--;
            } else if (!sameColourPiece(cell, cellRow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellRow, colptr));
                break;
            } else
                break;
        }
        colptr = cellCol + 1;
        while (colptr <= 7 ) {
            if (isEmpty(cellRow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellRow, colptr));
                colptr++;
            } else if (!sameColourPiece(cell, cellRow, colptr)) {
                legalToCoordinates.add(new Coordinate(cellRow, colptr));
                break;
            } else
                break;
        }

        rowptr = cellRow + 1;
        while (rowptr <= 7) {
            if (isEmpty(rowptr, cellCol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellCol));
                rowptr++;
            } else if (!sameColourPiece(cell, rowptr, cellCol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellCol));
                break;
            } else
                break;
        }

        rowptr = cellRow - 1;
        while (rowptr >= 0) {
            if (isEmpty(rowptr, cellCol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellCol));
                rowptr--;
            } else if (!sameColourPiece(cell, rowptr, cellCol)) {
                legalToCoordinates.add(new Coordinate(rowptr, cellCol));
                break;
            } else
                break;
        }

        rowptr = cellRow-1;
        colptr = cellCol-1;

        while(rowptr >= 0 && colptr >= 0){

            if(isEmpty(rowptr,colptr)) {
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                rowptr--;
                colptr--;
            }

            else if (!sameColourPiece(cell,rowptr,colptr)){
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                break;
            }

            else
                break;
        }

        rowptr = cellRow+1;
        colptr = cellCol-1;

        while(rowptr <= 7 && colptr >= 0){

            if(isEmpty(rowptr,colptr)) {
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                rowptr++;
                colptr--;
            }

            else if (!sameColourPiece(cell,rowptr,colptr)){
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                break;
            }

            else
                break;
        }

        rowptr = cellRow-1;
        colptr = cellCol+1;

        while(rowptr >= 0 && colptr <= 7){

            if(isEmpty(rowptr,colptr)) {
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                rowptr--;
                colptr++;
            }

            else if (!sameColourPiece(cell,rowptr,colptr)){
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                break;
            }

            else
                break;
        }

        rowptr = cellRow+1;
        colptr = cellCol+1;

        while(rowptr <= 7 && colptr <= 7){

            if(isEmpty(rowptr,colptr)) {
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                rowptr++;
                colptr++;
            }

            else if (!sameColourPiece(cell,rowptr,colptr)){
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                break;
            }

            else
                break;
        }

        List<Coordinate> pinnedcoordinates = isKingAttackedIfPieceRemoved(cell);

        if(pinnedcoordinates != null) {

            return intersection(pinnedcoordinates,legalToCoordinates);
            //fill this
        }
        return legalToCoordinates;

    }
}
