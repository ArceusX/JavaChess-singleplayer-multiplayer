package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.io.Serializable;
import java.util.ArrayList;

import static chess.game.logic.Board.isKingAttackedIfPieceRemoved;
import static chess.game.logic.Board.sameColourPiece;

public class Knight extends Piece implements Serializable {

    public Knight(Colour c) {

        name = ChessPiece.KNIGHT;
        if(c == Colour.BLACK) {
            image = createImageIcon("img/BlackKnight.png");
            colour = Colour.BLACK;
            isInPlay = true;
        }
        else {
            image = createImageIcon("img/WhiteKnight.png");
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
            if(cellcol-2 >= 0 && !sameColourPiece(c,cellrow-1,cellcol-2)) {
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol-2));
            }

            if(cellcol+2 <= 7 && !sameColourPiece(c,cellrow-1,cellcol+2)) {
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol+2));
            }
        }

        if(cellrow-2 >= 0) {
            if(cellcol-1 >= 0 && !sameColourPiece(c,cellrow-2,cellcol-1)) {
                legalToCoordinates.add(new Coordinate(cellrow-2,cellcol-1));
            }

            if(cellcol+1 <= 7 && !sameColourPiece(c,cellrow-2,cellcol+1)) {
                legalToCoordinates.add(new Coordinate(cellrow-2, cellcol+1));
            }
        }

        if(cellrow+1 <= 7) {
            if(cellcol-2 >= 0 && !sameColourPiece(c,cellrow+1,cellcol-2)) {
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol-2));
            }

            if(cellcol+2 <= 7 && !sameColourPiece(c,cellrow+1,cellcol+2)) {
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol+2));
            }
        }

        if(cellrow+2 <= 7) {
            if(cellcol-1 >= 0 && !sameColourPiece(c,cellrow+2,cellcol-1)) {
                legalToCoordinates.add(new Coordinate(cellrow+2,cellcol-1));
            }

            if(cellcol+1 <= 7 && !sameColourPiece(c,cellrow+2,cellcol+1)) {
                legalToCoordinates.add(new Coordinate(cellrow+2, cellcol+1));
            }
        }
        ArrayList<Coordinate> pinnedcoordinates = isKingAttackedIfPieceRemoved(c);

        if(pinnedcoordinates != null) {

            return intersection(pinnedcoordinates,legalToCoordinates);
            //fill this
        }

        return legalToCoordinates;
    }
}
