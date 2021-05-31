package chess.game.logic;

import chess.game.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import static chess.game.logic.Board.*;

/** Cell implements functionality of a Chess square and contains
 *  a button, its occupyingPiece (if there is one), its coordinate, and its board colour
 */

public class Cell implements ActionListener, Serializable {

    public JButton btn;
    public Piece occupyingPiece;
    Coordinate coordinate;
    Colour colour;
    public ArrayList<Coordinate> legalToCoordinates;

    public Cell(int row, int col) {
        btn = new JButton();
        occupyingPiece = null;
        coordinate = new Coordinate(row, col);
        colour = coordinate.getColour();
        legalToCoordinates = null;

        //When btn is pressed, border encircling the btn's icon won't be highlited.
        btn.setFocusPainted(false);
        btn.addActionListener(this);
    }

    void setBackgroundColour(int r, int g, int b) {
        btn.setBackground(new Color(r, g, b));
    }

    void addToJPanel(JPanel jp) {
        jp.add(btn);
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


                if (!ispromo())
                    removePieceAndAdd();
                else
                    removePieceAndPromote();

                if(checkmatedKingColour != Colour.NONE) {
                    if(isNetworked) {
                        sendMoveOnNetwork(null);
                    }

                    endGame();
                }

                if(!isNetworked)    //change turn only if its not network mode
                    changeTurn();
            } else {

                unHightliteCell(this);

                if (occupyingPiece != null && matchColour(this)) {
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

    Coordinate subtract(Cell c1) {
        int row = c1.getRow() - this.getRow();
        int col = c1.getCol() - this.getCol();

        return new Coordinate(row,col);
    }

    public ChessPiece getPieceName() {
        if(occupyingPiece == null)
            return null;
        else
            return occupyingPiece.name;
    }

    public Colour getPieceColour() {
        if(occupyingPiece == null)
            return Colour.NONE;
        else
            return occupyingPiece.colour;
    }

    public static boolean contains(ArrayList<Coordinate> C, Coordinate position) {       //we can't use highlightedcell.legalToCoordinates.contains(cellposition) because it compares the objects and not object.row & object.col

        if(C != null)
            for(Coordinate cord : C) {
                if(cord.row == position.row && cord.col == position.col)
                    return true;
            }

        return false;
    }

    boolean withinLegalCoordinates() {
        if(contains(cellSelected.legalToCoordinates, coordinate))     //if highlightedcell.legalToCoordinates contains cellposition
            return true;
        else
            return false;
    }

    boolean ispromo(){
        if (cellSelected.occupyingPiece.name == ChessPiece.PAWN) {
            if (cellSelected.occupyingPiece.colour == Colour.WHITE)
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
        ArrayList<Coordinate> movedcells = new ArrayList<Coordinate>();

        movedcells.add(cellSelected.coordinate);
        movedcells.add(this.coordinate);

        if(isNetworked)
            sendMoveOnNetwork(movedcells);

        cellSelected.btn.setIcon(null);   //removes icon from highlightedcell

        //if selected cell is king (some piece removes king), then set respective colour king as dead
        if(this.occupyingPiece != null && this.occupyingPiece.name == ChessPiece.KING ) {
            if(this.occupyingPiece.colour == Colour.WHITE)
                checkmatedKingColour = Colour.WHITE;
            else
                checkmatedKingColour = Colour.BLACK;
        }

        for(Coordinate coordinates : cellSelected.legalToCoordinates) {    //this will remove legalcells' background
            cells[coordinates.row][coordinates.col].btn.setBorder(BorderFactory.createEmptyBorder());
        }
        cellSelected.legalToCoordinates = null;

        setPiece(cellSelected.occupyingPiece.name, turn);    //sets piece in selected cell
        legalToCoordinates = occupyingPiece.getLegalMoves(this);

        cellSelected.occupyingPiece = null;

        unHightliteCell(this);
    }

    void removePieceAndPromote() {   //removes piece from highlighted cell and adds it to selected cell

        ArrayList<Coordinate> movedcells = new ArrayList<Coordinate>();

        movedcells.add(cellSelected.coordinate);
        movedcells.add(this.coordinate);


        cellSelected.btn.setIcon(null);   //removes icon from highlightedcell

        if(this.occupyingPiece != null && this.occupyingPiece.name == ChessPiece.KING ) {
            if(this.occupyingPiece.colour == Colour.WHITE)
                checkmatedKingColour = Colour.WHITE;
            else
                checkmatedKingColour = Colour.BLACK;
        }

        for(Coordinate coordinates : cellSelected.legalToCoordinates) {    //this will remove legalcells' background
            cells[coordinates.row][coordinates.col].btn.setBorder(BorderFactory.createEmptyBorder());
        }
        cellSelected.legalToCoordinates = null;

        ChessPiece promotedPiece = getPromopiece();
        setPiece(promotedPiece, turn);    //sets piece in selected cell
        legalToCoordinates = occupyingPiece.getLegalMoves(this);

        cellSelected.occupyingPiece = null;

        unHightliteCell(this);

        if(isNetworked)
            sendMoveOnNetwork(movedcells, promotedPiece);
    }

    ChessPiece getPromopiece(){
        ImageIcon img = new ImageIcon("img/PromotionPieces.png");

        String[] options = {"Queen", "Bishop","Knight", "Rook"};
        int row = JOptionPane.showOptionDialog(null, img ,"Pawn Promotion",JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
        System.out.println(row);
        switch (row){
            case 0: return ChessPiece.QUEEN;
            case 1: return  ChessPiece.KNIGHT;
            case 2: return ChessPiece.BISHOP;
            case 3: return ChessPiece.ROOK;
        }
        return null;
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
