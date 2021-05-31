package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.io.Serializable;
import java.util.ArrayList;

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
    public ArrayList<Coordinate> getLegalMoves(Cell cell) {

        ArrayList<Coordinate> legalToCoordinates = new ArrayList<Coordinate>();

        int cellrow = cell.getRow();
        int cellcol = cell.getCol();

        if(cellrow-1 >= 0) {
            //row above king
            if(cellcol-1 >= 0) {
                if(isEmpty(cellrow-1,cellcol-1) || !sameColourPiece(cell,cellrow-1,cellcol-1))
                    legalToCoordinates.add(new Coordinate(cellrow-1,cellcol-1));
            }

            if(isEmpty(cellrow-1,cellcol) || !sameColourPiece(cell,cellrow-1,cellcol))
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol));

            if(cellcol+1 < 8) {
                if(isEmpty(cellrow-1,cellcol+1) || !sameColourPiece(cell,cellrow-1,cellcol+1))
                    legalToCoordinates.add(new Coordinate(cellrow-1,cellcol+1));
            }
        }

        //same row as king

        if(cellcol-1 >= 0) {
            if(isEmpty(cellrow,cellcol-1) || !sameColourPiece(cell,cellrow,cellcol-1))
                legalToCoordinates.add(new Coordinate(cellrow,cellcol-1));
        }

        if(cellcol+1 < 8) {
            if(isEmpty(cellrow,cellcol+1) || !sameColourPiece(cell,cellrow,cellcol+1))
                legalToCoordinates.add(new Coordinate(cellrow,cellcol+1));
        }



        if(cellrow+1 < 8) {
            //row below king
            if(cellcol-1 >= 0) {
                if(isEmpty(cellrow+1,cellcol-1) || !sameColourPiece(cell,cellrow+1,cellcol-1))
                    legalToCoordinates.add(new Coordinate(cellrow+1,cellcol-1));
            }

            if(isEmpty(cellrow+1,cellcol) || !sameColourPiece(cell,cellrow+1,cellcol))
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol));

            if(cellcol+1 < 8) {
                if(isEmpty(cellrow+1,cellcol+1) || !sameColourPiece(cell,cellrow+1,cellcol+1))
                    legalToCoordinates.add(new Coordinate(cellrow+1,cellcol+1));
            }
        }



        //from all these legal moves, check if move results in a check.

        return legalToCoordinates;
    }
}
