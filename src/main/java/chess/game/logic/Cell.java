package chess.game.logic;

import chess.game.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import static chess.game.logic.Board.*;

/** Cell implements functionality of a Chess square and contains
 *  a button, its occupyingPiece (if there is one), its coordinate, and its board colour
 */

public class Cell implements ActionListener, Serializable {

    private final Board board;
    public JButton btn;
    public Piece occupyingPiece;
    Coordinate coordinate;
    Colour colour;
    public List<Coordinate> legalToCoordinates;

    public Cell(Board board, int row, int col) {
        this.board = board;

        btn = new JButton();
        occupyingPiece = null;
        coordinate = new Coordinate(row, col);
        colour = coordinate.getColour();
        legalToCoordinates = null;

        //When btn is pressed, border encircling the btn's icon won't be highlited.
        btn.setFocusPainted(false);
        btn.addActionListener(this);
    }

    void setBackgroundColor(Color color) {
        btn.setBackground(color);
    }

    void addToJPanel(JPanel panel) {
        panel.add(btn);
    }

    public void setPiece(ChessPiece P, Colour colour) {
        switch(P) {
        case PAWN:
            occupyingPiece = new Pawn(colour);
            break;

        case ROOK:
            occupyingPiece = new Rook(colour);
            break;

        case KNIGHT:
            occupyingPiece = new Knight(colour);
            break;

        case BISHOP:
            occupyingPiece = new Bishop(colour);
            break;

        case QUEEN:
            occupyingPiece = new Queen(colour);
            break;

        case KING:
            occupyingPiece = new King(colour);
            break;
        }

        if (occupyingPiece != null) setImage();
    }

    void setImage() {
        if(occupyingPiece.getImage() != null) {
            btn.setIcon(occupyingPiece.getImage());
            btn.setDisabledIcon(occupyingPiece.getImage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if (!hasSelected) {
          /*  if(occupyingPiece != null)
                legalToCoordinates = occupyingPiece.getLegalMoves(this);
          */  highliteCell(this);
        } else {
            /*
             *This occurs when user had already clicked a button and he clicks some other button.
             *
             * 1. he presses a button within the legal coordinates
             * 2. he presses a button other than legal coordinates
             *      a. he presses a button of same colour piece, then unhighlight previous pressed and highlight new one
             *      b. he presses some other invalid button, unhighlight the previous pressed button
             */
            
            //other than legal coordinates

            if(withinLegalCoordinates()) {

                if (!isPromotion()) {
                    removePieceAndAdd();
                }
                else {
                    removePieceAndPromote();
                }

                if (checkmatedKingColour != Colour.NONE) {
                    if (isNetworked) {
                        sendMoveOnNetwork(null);
                    }
                    endGame();
                }

                if (!isNetworked) {
                    passTurn();
                }
            }
            else {

                unHightliteCell(this);

                if (occupyingPiece != null && matchTurn(this)) {
                    highliteCell(this);
                }
            }
        }
    }

    public int getRow() {
        return coordinate.row;
    }

    public int getCol() {
        return coordinate.col;
    }

    Coordinate subtract(Cell fromCell) {

        int rowDifference = this.getRow() - fromCell.getRow();
        int colDifference = this.getCol() - fromCell.getCol();

        return new Coordinate(rowDifference, colDifference);
    }

    public ChessPiece getPieceName() {
        if (occupyingPiece == null)
            return null;
        else
            return occupyingPiece.name;
    }

    public Colour getPieceColour() {
        if (occupyingPiece == null)
            return Colour.NONE;
        else
            return occupyingPiece.colour;
    }

    public ImageIcon getPieceImage() {
        if (occupyingPiece == null)
            return null;
        else
            return occupyingPiece.image;
    }

    public static boolean contains(List<Coordinate> C, Coordinate position) {       //we can't use highlightedcell.legalToCoordinates.contains(cellposition) because it compares the objects and not object.row & object.col

        if (C != null) {
            for (Coordinate cord : C) {
                if(cord.row == position.row && cord.col == position.col) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean withinLegalCoordinates() {
        if(contains(cellSelected.legalToCoordinates, coordinate))     //if highlightedcell.legalToCoordinates contains cellposition
            return true;
        else
            return false;
    }

    boolean isPromotion() {
        if (cellSelected.getPieceName() == ChessPiece.PAWN) {
            if (cellSelected.getPieceColour() == Colour.WHITE)
                if (cellSelected.coordinate.row == 1)
                    return true;
                else
                    return false;

            else if (cellSelected.coordinate.row == 6)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    void removePieceAndAdd() {   //removes piece from highlighted cell and adds it to selected cell

        //removing piece from highlightedcell
        List<Coordinate> movedCells = new ArrayList<>();

        movedCells.add(cellSelected.coordinate);
        movedCells.add(this.coordinate);

        if(isNetworked) {
            sendMoveOnNetwork(movedCells);
        }

        cellSelected.btn.setIcon(null);   //removes icon from highlightedcell

        if (cellSelected.getPieceName() == ChessPiece.KING) {
            board.updateKingCell(cellSelected.getPieceColour(), this);
        }

        //if selected cell is king (some piece removes king), then set respective colour king as dead
        if (this.getPieceName() == ChessPiece.KING) {
            checkmatedKingColour = (this.getPieceColour() == Colour.WHITE) ? Colour.WHITE : Colour.BLACK;
        }

        for(Coordinate coordinates : cellSelected.legalToCoordinates) {    //this will remove legalcells' background
            cells[coordinates.row][coordinates.col].btn.setBorder(BorderFactory.createEmptyBorder());
        }
        cellSelected.legalToCoordinates = null;

        setPiece(cellSelected.getPieceName(), turn);    //sets piece in selected cell
        legalToCoordinates = occupyingPiece.getLegalMoves(this);

        cellSelected.occupyingPiece = null;

        unHightliteCell(this);
    }

    void removePieceAndPromote() {   //removes piece from highlighted cell and adds it to selected cell

        ArrayList<Coordinate> movedcells = new ArrayList<Coordinate>();

        movedcells.add(cellSelected.coordinate);
        movedcells.add(this.coordinate);


        cellSelected.btn.setIcon(null);   //removes icon from highlightedcell

        if(this.occupyingPiece != null && this.getPieceName() == ChessPiece.KING ) {
            if(this.getPieceColour() == Colour.WHITE)
                checkmatedKingColour = Colour.WHITE;
            else
                checkmatedKingColour = Colour.BLACK;
        }

        for(Coordinate coordinates : cellSelected.legalToCoordinates) {    //this will remove legalcells' background
            cells[coordinates.row][coordinates.col].btn.setBorder(BorderFactory.createEmptyBorder());
        }
        cellSelected.legalToCoordinates = null;

        ChessPiece promotedPiece = getPromotion();
        setPiece(promotedPiece, turn);    //sets piece in selected cell
        legalToCoordinates = occupyingPiece.getLegalMoves(this);

        cellSelected.occupyingPiece = null;

        unHightliteCell(this);

        if(isNetworked)
            sendMoveOnNetwork(movedcells, promotedPiece);
    }

    ChessPiece getPromotion() {

        String[] options = {"Queen", "Bishop", "Knight", "Rook"};
        int selected = JOptionPane.showOptionDialog(null, null, "Promote to...",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("img/PromotionPieces.png"), options, options[0]);

        switch (selected) {
            case 0: return ChessPiece.QUEEN;
            case 1: return ChessPiece.BISHOP;
            case 2: return  ChessPiece.KNIGHT;
            case 3: return ChessPiece.ROOK;
            default: return null;
        }
    }

    void endGame() {
        if(checkmatedKingColour == Colour.WHITE) {
            JOptionPane.showMessageDialog(null,"Black wins!");
        } else {
            JOptionPane.showMessageDialog(null, "White wins!");
        }
        System.exit(0);
    }
}
