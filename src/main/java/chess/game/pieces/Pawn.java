package chess.game.pieces;

import chess.game.Cell;
import chess.game.Colour;
import chess.game.Coordinate;
import chess.game.ChessPiece;

import java.util.ArrayList;

import static chess.game.Board.isEmpty;
import static chess.game.Board.isKingAttackedIfPieceRemoved;
import static chess.game.Board.sameColourPiece;

public class Pawn extends Piece {

    public Pawn(Colour c) {

        name = ChessPiece.PAWN;
        //black pawn
        if(c == Colour.BLACK) {
            image = createImageIcon("img/BlackPawn.png");
            colour = Colour.BLACK;
            isInPlay = true;
        }
        else {  //white pawn
            image = createImageIcon("img/WhitePawn.png");
            colour = Colour.WHITE;
            isInPlay = true;
        }

    }

    @Override
    public ArrayList<Coordinate> legalMoves(Cell c) {

        ArrayList<Coordinate> legalToCoordinates = new ArrayList<Coordinate>();
        int cellrow = c.getRow();
        int cellcol = c.getCol();

        if (colour == Colour.WHITE)
        {
            if (isEmpty(cellrow-1,cellcol)) {

                if(cellrow ==6 && isEmpty(cellrow-2,cellcol))
                    legalToCoordinates.add(new Coordinate(cellrow-2,cellcol));

                legalToCoordinates.add(new Coordinate(cellrow - 1, cellcol));
            }
            if(cellcol > 0 && !sameColourPiece(c,cellrow-1,cellcol-1) && !isEmpty(cellrow-1,cellcol-1))
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol-1));

            if(cellcol < 7 && !sameColourPiece(c,cellrow-1,cellcol+1) && !isEmpty(cellrow-1,cellcol+1))
                legalToCoordinates.add(new Coordinate(cellrow-1,cellcol+1));

            //if(cellrow == 1)  pawn promotion
        }

        else
        {
            if (isEmpty(cellrow+1,cellcol)) {

                if(cellrow ==1 && isEmpty(cellrow+2,cellcol) )
                    legalToCoordinates.add(new Coordinate(cellrow+2,cellcol));

                legalToCoordinates.add(new Coordinate(cellrow + 1, cellcol));

            }

            if(cellcol > 0 && !sameColourPiece(c,cellrow+1,cellcol-1) && !isEmpty(cellrow+1,cellcol-1))
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol-1));

            if(cellcol < 7 && !sameColourPiece(c,cellrow+1,cellcol+1) && !isEmpty(cellrow+1,cellcol+1))
                legalToCoordinates.add(new Coordinate(cellrow+1,cellcol+1));

        }

        ArrayList<Coordinate> pinnedcoordinates = isKingAttackedIfPieceRemoved(c);

        if(pinnedcoordinates != null) {

            return intersection(pinnedcoordinates,legalToCoordinates);
            //fill this
        }

        return legalToCoordinates;
    }

}
