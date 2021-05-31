package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.io.Serializable;
import java.util.List;
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
    public List<Coordinate> getLegalMoves(Cell cell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();
        int cellRow = cell.getRow();
        int cellCol = cell.getCol();

        if(cellRow-1 >= 0) {
            if(cellCol-2 >= 0 && !sameColourPiece(cell,cellRow-1,cellCol-2)) {
                legalToCoordinates.add(new Coordinate(cellRow-1,cellCol-2));
            }

            if(cellCol+2 <= 7 && !sameColourPiece(cell,cellRow-1,cellCol+2)) {
                legalToCoordinates.add(new Coordinate(cellRow-1,cellCol+2));
            }
        }

        if(cellRow-2 >= 0) {
            if(cellCol-1 >= 0 && !sameColourPiece(cell,cellRow-2,cellCol-1)) {
                legalToCoordinates.add(new Coordinate(cellRow-2,cellCol-1));
            }

            if(cellCol+1 <= 7 && !sameColourPiece(cell,cellRow-2,cellCol+1)) {
                legalToCoordinates.add(new Coordinate(cellRow-2, cellCol+1));
            }
        }

        if(cellRow+1 <= 7) {
            if(cellCol-2 >= 0 && !sameColourPiece(cell,cellRow+1,cellCol-2)) {
                legalToCoordinates.add(new Coordinate(cellRow+1,cellCol-2));
            }

            if(cellCol+2 <= 7 && !sameColourPiece(cell,cellRow+1,cellCol+2)) {
                legalToCoordinates.add(new Coordinate(cellRow+1,cellCol+2));
            }
        }

        if(cellRow+2 <= 7) {
            if(cellCol-1 >= 0 && !sameColourPiece(cell,cellRow+2,cellCol-1)) {
                legalToCoordinates.add(new Coordinate(cellRow+2,cellCol-1));
            }

            if(cellCol+1 <= 7 && !sameColourPiece(cell,cellRow+2,cellCol+1)) {
                legalToCoordinates.add(new Coordinate(cellRow+2, cellCol+1));
            }
        }
        List<Coordinate> pinnedCoordinates = isKingAttackedIfPieceRemoved(cell);

        if(pinnedCoordinates != null) {
            return intersection(pinnedCoordinates,legalToCoordinates);
        }

        return legalToCoordinates;
    }
}
