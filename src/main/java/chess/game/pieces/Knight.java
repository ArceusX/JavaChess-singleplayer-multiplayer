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

        if (colour == Colour.WHITE) {
            image = createImageIcon("img/WhiteKnight.png");
            this.colour = Colour.WHITE;

        }
        else {
            image = createImageIcon("img/BlackKnight.png");
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
            if(cellcol-2 >= 0 && !sameColourPiece(cell,cellrow-1,cellcol-2)) {
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol-2));
            }

            if(cellcol+2 <= 7 && !sameColourPiece(cell,cellrow-1,cellcol+2)) {
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol+2));
            }
        }

        if(cellrow-2 >= 0) {
            if(cellcol-1 >= 0 && !sameColourPiece(cell,cellrow-2,cellcol-1)) {
                legalToCoordinates.add(new Coordinate(cellrow-2,cellcol-1));
            }

            if(cellcol+1 <= 7 && !sameColourPiece(cell,cellrow-2,cellcol+1)) {
                legalToCoordinates.add(new Coordinate(cellrow-2, cellcol+1));
            }
        }

        if(cellrow+1 <= 7) {
            if(cellcol-2 >= 0 && !sameColourPiece(cell,cellrow+1,cellcol-2)) {
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol-2));
            }

            if(cellcol+2 <= 7 && !sameColourPiece(cell,cellrow+1,cellcol+2)) {
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol+2));
            }
        }

        if(cellrow+2 <= 7) {
            if(cellcol-1 >= 0 && !sameColourPiece(cell,cellrow+2,cellcol-1)) {
                legalToCoordinates.add(new Coordinate(cellrow+2,cellcol-1));
            }

            if(cellcol+1 <= 7 && !sameColourPiece(cell,cellrow+2,cellcol+1)) {
                legalToCoordinates.add(new Coordinate(cellrow+2, cellcol+1));
            }
        }
        ArrayList<Coordinate> pinnedcoordinates = isKingAttackedIfPieceRemoved(cell);

        if(pinnedcoordinates != null) {

            return intersection(pinnedcoordinates,legalToCoordinates);
            //fill this
        }

        return legalToCoordinates;
    }
}
