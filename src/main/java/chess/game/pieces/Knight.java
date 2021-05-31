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
    public List<Coordinate> getLegalMoves(Cell fromCell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();
        int row = fromCell.getRow();
        int col = fromCell.getCol();

        if(row-1 >= 0) {
            if(col-2 >= 0 && !sameColourPiece(fromCell,row-1,col-2)) {
                legalToCoordinates.add(new Coordinate(row-1,col-2));
            }

            if(col+2 <= 7 && !sameColourPiece(fromCell,row-1,col+2)) {
                legalToCoordinates.add(new Coordinate(row-1,col+2));
            }
        }

        if(row-2 >= 0) {
            if(col-1 >= 0 && !sameColourPiece(fromCell,row-2,col-1)) {
                legalToCoordinates.add(new Coordinate(row-2,col-1));
            }

            if(col+1 <= 7 && !sameColourPiece(fromCell,row-2,col+1)) {
                legalToCoordinates.add(new Coordinate(row-2, col+1));
            }
        }

        if(row+1 <= 7) {
            if(col-2 >= 0 && !sameColourPiece(fromCell,row+1,col-2)) {
                legalToCoordinates.add(new Coordinate(row+1,col-2));
            }

            if(col+2 <= 7 && !sameColourPiece(fromCell,row+1,col+2)) {
                legalToCoordinates.add(new Coordinate(row+1,col+2));
            }
        }

        if(row+2 <= 7) {
            if(col-1 >= 0 && !sameColourPiece(fromCell,row+2,col-1)) {
                legalToCoordinates.add(new Coordinate(row+2,col-1));
            }

            if(col+1 <= 7 && !sameColourPiece(fromCell,row+2,col+1)) {
                legalToCoordinates.add(new Coordinate(row+2, col+1));
            }
        }
        List<Coordinate> pinnedCoordinates = isKingAttackedIfPieceRemoved(fromCell);

        if(pinnedCoordinates != null) {
            return intersection(pinnedCoordinates,legalToCoordinates);
        }

        return legalToCoordinates;
    }
}
