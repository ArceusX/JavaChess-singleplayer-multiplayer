package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.util.ArrayList;
import java.util.List;

import static chess.game.logic.Board.isEmpty;
import static chess.game.logic.Board.isKingAttackedIfPieceRemoved;
import static chess.game.logic.Board.sameColourPiece;

public class Pawn extends Piece {

    public Pawn(Colour colour) {

        name = ChessPiece.PAWN;
        if (colour == Colour.WHITE) {
            image = createImageIcon("img/WhitePawn.png");
            this.colour = Colour.WHITE;
        }
        else {
            image = createImageIcon("img/BlackPawn.png");
            this.colour = Colour.BLACK;
        }

        isInPlay = true;

    }

    @Override
    public List<Coordinate> getLegalMoves(Cell fromCell) {

        ArrayList<Coordinate> legalToCoordinates = new ArrayList<>();
        int row = fromCell.getRow();
        int col = fromCell.getCol();

        if (colour == Colour.WHITE) {
            if (isEmpty(row-1,col)) {

                if(row ==6 && isEmpty(row-2,col))
                    legalToCoordinates.add(new Coordinate(row-2,col));

                legalToCoordinates.add(new Coordinate(row - 1, col));
            }
            if(col > 0 && !sameColourPiece(fromCell,row-1,col-1) && !isEmpty(row-1,col-1))
                legalToCoordinates.add(new Coordinate(row-1,col-1));

            if(col < 7 && !sameColourPiece(fromCell,row-1,col+1) && !isEmpty(row-1,col+1))
                legalToCoordinates.add(new Coordinate(row-1,col+1));

            //if(row == 1)  pawn promotion
        }

        else {
            if (isEmpty(row+1,col)) {

                if(row ==1 && isEmpty(row+2,col) )
                    legalToCoordinates.add(new Coordinate(row+2,col));

                legalToCoordinates.add(new Coordinate(row + 1, col));

            }

            if(col > 0 && !sameColourPiece(fromCell,row+1,col-1) && !isEmpty(row+1,col-1))
                legalToCoordinates.add(new Coordinate(row+1,col-1));

            if(col < 7 && !sameColourPiece(fromCell,row+1,col+1) && !isEmpty(row+1,col+1))
                legalToCoordinates.add(new Coordinate(row+1,col+1));

        }

        List<Coordinate> pinnedCoordinates = isKingAttackedIfPieceRemoved(fromCell);

        if(pinnedCoordinates != null) {
            return intersection(pinnedCoordinates, legalToCoordinates);
        }

        return legalToCoordinates;
    }

}
