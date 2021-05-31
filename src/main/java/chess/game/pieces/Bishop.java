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
    public List<Coordinate> getLegalMoves(Cell fromCell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();
        int row = fromCell.getRow();
        int col = fromCell.getCol();

        int rowPtr;
        int colPtr;

        rowPtr = row-1;
        colPtr = col-1;

        while (rowPtr >= 0 && colPtr >= 0){

            if(isEmpty(rowPtr,colPtr)) {
                legalToCoordinates.add(new Coordinate(rowPtr, colPtr));
                rowPtr--;
                colPtr--;
            }

            else if (!isMatchedColour(fromCell,rowPtr,colPtr)){
                legalToCoordinates.add(new Coordinate(rowPtr, colPtr));
                break;
            }

            else
                break;
        }

        rowPtr = row+1;
        colPtr = col-1;

        while(rowPtr <= 7 && colPtr >= 0){

            if(isEmpty(rowPtr,colPtr)) {
                legalToCoordinates.add(new Coordinate(rowPtr, colPtr));
                rowPtr++;
                colPtr--;
            }

            else if (!isMatchedColour(fromCell,rowPtr,colPtr)){
                legalToCoordinates.add(new Coordinate(rowPtr, colPtr));
                break;
            }

            else
                break;
        }

        rowPtr = row-1;
        colPtr = col+1;

        while(rowPtr >= 0 && colPtr <= 7){

            if(isEmpty(rowPtr,colPtr)) {
                legalToCoordinates.add(new Coordinate(rowPtr, colPtr));
                rowPtr--;
                colPtr++;
            }

            else if (!isMatchedColour(fromCell,rowPtr,colPtr)){
                legalToCoordinates.add(new Coordinate(rowPtr, colPtr));
                break;
            }

            else
                break;
        }

        rowPtr = row+1;
        colPtr = col+1;

        while(rowPtr <= 7 && colPtr <= 7){

            if(isEmpty(rowPtr,colPtr)) {
                legalToCoordinates.add(new Coordinate(rowPtr, colPtr));
                rowPtr++;
                colPtr++;
            }

            else if (!isMatchedColour(fromCell,rowPtr,colPtr)){
                legalToCoordinates.add(new Coordinate(rowPtr, colPtr));
                break;
            }

            else
                break;
        }

        List<Coordinate> pinnedCoordinates = isKingAttackedIfPieceRemoved(fromCell);

        if(pinnedCoordinates != null) {

            return intersection(pinnedCoordinates,legalToCoordinates);
        }
        return legalToCoordinates;
    }
}
