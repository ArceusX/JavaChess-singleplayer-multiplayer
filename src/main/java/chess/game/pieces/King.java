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
    public List<Coordinate> getLegalMoves(Cell cell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();

        int cellRow = cell.getRow();
        int cellCol = cell.getCol();

        if(cellRow-1 >= 0) {
            //row above king
            if(cellCol-1 >= 0) {
                if(isEmpty(cellRow-1,cellCol-1) || !sameColourPiece(cell,cellRow-1,cellCol-1))
                    legalToCoordinates.add(new Coordinate(cellRow-1,cellCol-1));
            }

            if(isEmpty(cellRow-1,cellCol) || !sameColourPiece(cell,cellRow-1,cellCol))
                legalToCoordinates.add(new Coordinate(cellRow-1,cellCol));

            if(cellCol+1 < 8) {
                if(isEmpty(cellRow-1,cellCol+1) || !sameColourPiece(cell,cellRow-1,cellCol+1))
                    legalToCoordinates.add(new Coordinate(cellRow-1,cellCol+1));
            }
        }

        //same row as king

        if(cellCol-1 >= 0) {
            if(isEmpty(cellRow,cellCol-1) || !sameColourPiece(cell,cellRow,cellCol-1))
                legalToCoordinates.add(new Coordinate(cellRow,cellCol-1));
        }

        if(cellCol+1 < 8) {
            if(isEmpty(cellRow,cellCol+1) || !sameColourPiece(cell,cellRow,cellCol+1))
                legalToCoordinates.add(new Coordinate(cellRow,cellCol+1));
        }

        if(cellRow+1 < 8) {
            //row below king
            if(cellCol-1 >= 0) {
                if(isEmpty(cellRow+1,cellCol-1) || !sameColourPiece(cell,cellRow+1,cellCol-1))
                    legalToCoordinates.add(new Coordinate(cellRow+1,cellCol-1));
            }

            if(isEmpty(cellRow+1,cellCol) || !sameColourPiece(cell,cellRow+1,cellCol))
                legalToCoordinates.add(new Coordinate(cellRow+1,cellCol));

            if(cellCol+1 < 8) {
                if(isEmpty(cellRow+1,cellCol+1) || !sameColourPiece(cell,cellRow+1,cellCol+1))
                    legalToCoordinates.add(new Coordinate(cellRow+1,cellCol+1));
            }
        }

        //from all these legal moves, check if move results in a check.

        return legalToCoordinates;
    }
}
