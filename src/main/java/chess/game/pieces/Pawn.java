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
    public List<Coordinate> getLegalMoves(Cell cell) {

        ArrayList<Coordinate> legalToCoordinates = new ArrayList<Coordinate>();
        int cellrow = cell.getRow();
        int cellcol = cell.getCol();

        if (colour == Colour.WHITE)
        {
            if (isEmpty(cellrow-1,cellcol)) {

                if(cellrow ==6 && isEmpty(cellrow-2,cellcol))
                    legalToCoordinates.add(new Coordinate(cellrow-2,cellcol));

                legalToCoordinates.add(new Coordinate(cellrow - 1, cellcol));
            }
            if(cellcol > 0 && !sameColourPiece(cell,cellrow-1,cellcol-1) && !isEmpty(cellrow-1,cellcol-1))
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol-1));

            if(cellcol < 7 && !sameColourPiece(cell,cellrow-1,cellcol+1) && !isEmpty(cellrow-1,cellcol+1))
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol+1));

            //if(cellrow == 1)  pawn promotion
        }

        else {
            if (isEmpty(cellrow+1,cellcol)) {

                if(cellrow ==1 && isEmpty(cellrow+2,cellcol) )
                    legalToCoordinates.add(new Coordinate(cellrow+2,cellcol));

                legalToCoordinates.add(new Coordinate(cellrow + 1, cellcol));

            }

            if(cellcol > 0 && !sameColourPiece(cell,cellrow+1,cellcol-1) && !isEmpty(cellrow+1,cellcol-1))
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol-1));

            if(cellcol < 7 && !sameColourPiece(cell,cellrow+1,cellcol+1) && !isEmpty(cellrow+1,cellcol+1))
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol+1));

        }

        List<Coordinate> pinnedCoordinates = isKingAttackedIfPieceRemoved(cell);

        if(pinnedCoordinates != null) {
            return intersection(pinnedCoordinates, legalToCoordinates);
        }

        return legalToCoordinates;
    }

}
