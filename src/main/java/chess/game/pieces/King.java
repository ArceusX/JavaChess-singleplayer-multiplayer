package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static chess.game.logic.Board.isEmpty;
import static chess.game.logic.Board.sameColourPiece;

public class King extends Piece implements Serializable {
    public King(Colour colour) {
        name = ChessPiece.KING;

        if (colour == Colour.WHITE) {
            image = createImageIcon("img/WhiteKing.png");
            this.colour = Colour.WHITE;

        }
        else {
            image = createImageIcon("img/BlackKing.png");
            this.colour = Colour.BLACK;
        }

        isInPlay = true;
    }

    @Override
    public List<Coordinate> getLegalMoves(Cell fromCell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();

        int row = fromCell.getRow();
        int col = fromCell.getCol();

        if(row-1 >= 0) {
            //row above king
            if(col-1 >= 0) {
                if(isEmpty(row-1,col-1) || !sameColourPiece(fromCell,row-1,col-1))
                    legalToCoordinates.add(new Coordinate(row-1,col-1));
            }

            if(isEmpty(row-1,col) || !sameColourPiece(fromCell,row-1,col))
                legalToCoordinates.add(new Coordinate(row-1,col));

            if(col+1 < 8) {
                if(isEmpty(row-1,col+1) || !sameColourPiece(fromCell,row-1,col+1))
                    legalToCoordinates.add(new Coordinate(row-1,col+1));
            }
        }

        //same row as king

        if(col-1 >= 0) {
            if(isEmpty(row,col-1) || !sameColourPiece(fromCell,row,col-1))
                legalToCoordinates.add(new Coordinate(row,col-1));
        }

        if(col+1 < 8) {
            if(isEmpty(row,col+1) || !sameColourPiece(fromCell,row,col+1))
                legalToCoordinates.add(new Coordinate(row,col+1));
        }

        if(row+1 < 8) {
            //row below king
            if(col-1 >= 0) {
                if(isEmpty(row+1,col-1) || !sameColourPiece(fromCell,row+1,col-1))
                    legalToCoordinates.add(new Coordinate(row+1,col-1));
            }

            if(isEmpty(row+1,col) || !sameColourPiece(fromCell,row+1,col))
                legalToCoordinates.add(new Coordinate(row+1,col));

            if(col+1 < 8) {
                if(isEmpty(row+1,col+1) || !sameColourPiece(fromCell,row+1,col+1))
                    legalToCoordinates.add(new Coordinate(row+1,col+1));
            }
        }

        //from all these legal moves, check if move results in a check.

        return legalToCoordinates;
    }
}
