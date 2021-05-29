package chess.game.pieces;

import chess.game.Cell;
import chess.game.Colour;
import chess.game.Coordinate;
import chess.game.ChessPiece;

import java.io.Serializable;
import java.util.ArrayList;

import static chess.game.Board.isEmpty;
import static chess.game.Board.isKingAttackedIfPieceRemoved;
import static chess.game.Board.sameColourPiece;

public class Queen extends Piece implements Serializable {
    public Queen(Colour c) {

        name = ChessPiece.QUEEN;
        if(c == Colour.BLACK) {
            image = createImageIcon("img/BlackQueen.png");
            colour = Colour.BLACK;
            isInPlay = true;
        }
        else {
            image = createImageIcon("img/WhiteQueen.png");
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
        while (colptr <= 7 ) {
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

        rowptr = cellrow-1;
        colptr = cellcol-1;

        while(rowptr >= 0 && colptr >= 0){

            if(isEmpty(rowptr,colptr)) {
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                rowptr--;
                colptr--;
            }

            else if (!sameColourPiece(c,rowptr,colptr)){
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                break;
            }

            else
                break;
        }

        rowptr = cellrow+1;
        colptr = cellcol-1;

        while(rowptr <= 7 && colptr >= 0){

            if(isEmpty(rowptr,colptr)) {
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                rowptr++;
                colptr--;
            }

            else if (!sameColourPiece(c,rowptr,colptr)){
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                break;
            }

            else
                break;
        }

        rowptr = cellrow-1;
        colptr = cellcol+1;

        while(rowptr >= 0 && colptr <= 7){

            if(isEmpty(rowptr,colptr)) {
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                rowptr--;
                colptr++;
            }

            else if (!sameColourPiece(c,rowptr,colptr)){
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                break;
            }

            else
                break;
        }

        rowptr = cellrow+1;
        colptr = cellcol+1;

        while(rowptr <= 7 && colptr <= 7){

            if(isEmpty(rowptr,colptr)) {
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                rowptr++;
                colptr++;
            }

            else if (!sameColourPiece(c,rowptr,colptr)){
                legalToCoordinates.add(new Coordinate(rowptr, colptr));
                break;
            }

            else
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
