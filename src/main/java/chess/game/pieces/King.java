package chess.game.pieces;

import chess.game.Cell;
import chess.game.Colour;
import chess.game.Coordinate;
import chess.game.ChessPiece;

import java.io.Serializable;
import java.util.ArrayList;

import static chess.game.Board.isEmpty;
import static chess.game.Board.sameColourPiece;

public class King extends Piece implements Serializable {
    public King(Colour c) {

        name = ChessPiece.KING;
        if(c == Colour.BLACK) {
            image = createImageIcon("img/BlackKing.png");
            colour = Colour.BLACK;
            isInPlay = true;
        }
        else {
            image = createImageIcon("img/WhiteKing.png");
            colour = Colour.WHITE;
            isInPlay = true;
        }
    }

    @Override
    public ArrayList<Coordinate> legalMoves(Cell c) {

        ArrayList<Coordinate> legalToCoordinates = new ArrayList<Coordinate>();

        int cellrow = c.getRow();
        int cellcol = c.getCol();

        if(cellrow-1 >= 0) {
            //row above king
            if(cellcol-1 >= 0) {
                if(isEmpty(cellrow-1,cellcol-1) || !sameColourPiece(c,cellrow-1,cellcol-1))
                    legalToCoordinates.add(new Coordinate(cellrow-1,cellcol-1));
            }

            if(isEmpty(cellrow-1,cellcol) || !sameColourPiece(c,cellrow-1,cellcol))
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol));

            if(cellcol+1 < 8) {
                if(isEmpty(cellrow-1,cellcol+1) || !sameColourPiece(c,cellrow-1,cellcol+1))
                    legalToCoordinates.add(new Coordinate(cellrow-1,cellcol+1));
            }
        }

        //same row as king

        if(cellcol-1 >= 0) {
            if(isEmpty(cellrow,cellcol-1) || !sameColourPiece(c,cellrow,cellcol-1))
                legalToCoordinates.add(new Coordinate(cellrow,cellcol-1));
        }

        if(cellcol+1 < 8) {
            if(isEmpty(cellrow,cellcol+1) || !sameColourPiece(c,cellrow,cellcol+1))
                legalToCoordinates.add(new Coordinate(cellrow,cellcol+1));
        }



        if(cellrow+1 < 8) {
            //row below king
            if(cellcol-1 >= 0) {
                if(isEmpty(cellrow+1,cellcol-1) || !sameColourPiece(c,cellrow+1,cellcol-1))
                    legalToCoordinates.add(new Coordinate(cellrow+1,cellcol-1));
            }

            if(isEmpty(cellrow+1,cellcol) || !sameColourPiece(c,cellrow+1,cellcol))
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol));

            if(cellcol+1 < 8) {
                if(isEmpty(cellrow+1,cellcol+1) || !sameColourPiece(c,cellrow+1,cellcol+1))
                    legalToCoordinates.add(new Coordinate(cellrow+1,cellcol+1));
            }
        }



        //from all these legal moves, check if move results in a check.

        return legalToCoordinates;
    }
}
