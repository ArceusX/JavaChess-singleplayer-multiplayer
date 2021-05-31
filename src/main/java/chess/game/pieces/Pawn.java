package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import java.util.ArrayList;
import java.util.List;

import static chess.game.logic.Board.isEmpty;
import static chess.game.logic.Board.isKingAttackedIfPieceRemoved;
import static chess.game.logic.Board.isMatchedColour;

public class Pawn extends Piece {

    public Pawn(Colour colour) {
        super(colour);

        name = ChessPiece.PAWN;
        image = (colour == Colour.WHITE) ?
                createImageIcon("img/WhitePawn.png") : createImageIcon("img/BlackPawn.png");
    }

    @Override
    public List<Coordinate> getLegalMoves(Cell fromCell) {

        List<Coordinate> legalToCoordinates = new ArrayList<>();
        int row = fromCell.getRow();
        int col = fromCell.getCol();

        int dirForward = (colour == Colour.WHITE) ? -1 : 1;
        int rowStart = (colour == Colour.WHITE) ? 6 : 1;

        if (isEmpty(row + dirForward, col)) {

            //Pawn is at its original cell (6 for White, 1 for Black) and thus is known to not have already moved
            if (row == rowStart && isEmpty(row + 2 * dirForward, col)) {
                legalToCoordinates.add(new Coordinate(row + 2 * dirForward, col));
            }

            legalToCoordinates.add(new Coordinate(row + dirForward, col));
        }

        if(col > 0 && !isEmpty(row + dirForward, col - 1) &&
                !isMatchedColour(fromCell, row + dirForward, col - 1)) {
            legalToCoordinates.add(new Coordinate(row + dirForward, col - 1));
        }

        if(col < 7 && !isEmpty(row + dirForward, col + 1)
                && !isMatchedColour(fromCell,row + dirForward, col + 1)) {
            legalToCoordinates.add(new Coordinate(row + dirForward, col + 1));
        }

        //----------------------------------------------

        List<Coordinate> pinnedCoordinates = isKingAttackedIfPieceRemoved(fromCell);

        if(pinnedCoordinates != null) {
            return intersection(pinnedCoordinates, legalToCoordinates);
        }

        return legalToCoordinates;
    }
}
