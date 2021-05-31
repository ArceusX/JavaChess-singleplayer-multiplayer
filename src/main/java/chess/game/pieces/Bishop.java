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

public class Bishop extends Piece implements Serializable {
    public Bishop(Colour colour) {

        name = ChessPiece.BISHOP;
        if (colour == Colour.WHITE) {
            image = createImageIcon("img/WhiteBishop.png");
            this.colour = Colour.WHITE;

        }
        else {
            image = createImageIcon("img/BlackBishop.png");
            this.colour = Colour.BLACK;
        }

        isInPlay = true;
    }

    @Override
    public ArrayList<Coordinate> getLegalMoves(Cell cell) {

        ArrayList<Coordinate> legalToCoordinates = new ArrayList<>();
        int cellRow = cell.getRow();
        int cellCol = cell.getCol();

        int rowptr;
        int colptr;

        rowptr = cellRow-1;
        colptr = cellCol-1;

        while (rowptr >= 0 && colptr >= 0){

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

        ArrayList<Coordinate> pinnedcoordinates = isKingAttackedIfPieceRemoved(cell);

        if(pinnedcoordinates != null) {

            return intersection(pinnedcoordinates,legalToCoordinates);
        }
        return legalToCoordinates;
    }
}
