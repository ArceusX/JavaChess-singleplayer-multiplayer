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
    public Bishop(Colour c) {

        name = ChessPiece.BISHOP;
        if(c == Colour.BLACK) {
            image = createImageIcon("img/BlackBishop.png");
            colour = Colour.BLACK;
            isInPlay = true;
        }
        else {
            image = createImageIcon("img/WhiteBishop.png");
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
