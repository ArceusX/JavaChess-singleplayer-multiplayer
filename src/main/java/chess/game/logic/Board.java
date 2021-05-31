package chess.game.logic;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import static chess.game.network.Connect.modifyConnectNetworkTurn;
import static chess.game.network.Host.modifyHostNetworkTurn;
import static java.lang.Math.abs;

public class Board {
    public static Board board;
    public JPanel panel;
    static Color unselectedColor;

    static boolean hasSelected;
    static JButton btnSelected;

    static Cell cellSelected;
    //The selected cell changes colour
    public static Cell[][] cells;
    static Cell wKingCell;
    static Cell bKingCell;

    //Track whose turn it is to move
    static Colour turn;
    public static Colour checkmatedKingColour;

    //Only send data to the other socket if the game isNetworked
    public static boolean isNetworked;
    public static boolean isHost;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;

    public Board() {
        board = this;
        panel = new JPanel();
        unselectedColor = null;

        hasSelected = false;
        btnSelected = null;
        cellSelected = null;
        cells = new Cell[8][8];

        //White makes the first move, by convention
        turn = Colour.WHITE;
        checkmatedKingColour = Colour.NONE;

        //isNetworked will be set to true if this default constructor was called by the "networked" constructor
        isNetworked = false;

        panel.setSize(800, 800);
        panel.setLayout(new GridLayout(8, 8));

        drawBoardAndAddToPanel(cells);

        panel.setVisible(true);
    }

    public Board(ObjectInputStream input, ObjectOutputStream output, boolean host) {
        //Telescoping constructor
        this();
        this.inputStream = input;
        this.outputStream = output;
        isNetworked = true;

        if(host) {
            isHost = true;
            turn = Colour.WHITE;
        } else {
            isHost = false;
            turn = Colour.BLACK;
        }
    }

    //Send network data to the opponent
    public static void sendMoveOnNetwork(ArrayList<Coordinate> movedCells, ChessPiece promotionPiece) {
        try {
            board.outputStream.flush();
            board.outputStream.writeObject(movedCells);
            board.outputStream.writeObject(promotionPiece);

            if(isHost) {
                modifyHostNetworkTurn(Colour.BLACK);
            } else {
                modifyConnectNetworkTurn(Colour.WHITE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMoveOnNetwork(ArrayList<Coordinate> movedCells) {
        try {
            board.outputStream.flush();
            board.outputStream.writeObject(movedCells);

            if(isHost) {
                modifyHostNetworkTurn(Colour.BLACK);
            } else {
                modifyConnectNetworkTurn(Colour.WHITE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isPromotion(Cell fromCell) {
        if (fromCell.occupyingPiece.name == ChessPiece.PAWN) {
            //If a Pawn moved from its respective second to final row (1 for White, 6 for Black)
            if (fromCell.occupyingPiece.colour == Colour.WHITE) {
                return (fromCell.coordinate.row == 1) ? true : false;
            }
            else {
                return (fromCell.coordinate.row == 6) ? true : false;
            }
        }
        else {
            return false;
        }
    }

    //Receive network data from the opponent
    public static Colour updateBoardFromNetwork(Coordinate fromCoordinate, Coordinate toCoordinate) {

        Cell fromCell = cells[fromCoordinate.row][fromCoordinate.col];
        Cell toCell = cells[toCoordinate.row][toCoordinate.col];

        if(isPromotion(fromCell)) {
            try {
                //Track the type of promotedPiece, be it a Queen, Knight, Bishop, or Rook
                ChessPiece promotedPiece = (ChessPiece) board.inputStream.readObject();

                fromCell.btn.setIcon(null);   //removes icon from highlitedcell
                if(toCell.occupyingPiece != null && toCell.occupyingPiece.name == ChessPiece.KING ) {
                    toCell.btn.setIcon(fromCell.occupyingPiece.getImage());
                    if(toCell.occupyingPiece.colour == Colour.WHITE)
                        return Colour.BLACK;
                    else
                        return Colour.WHITE;
                }

                fromCell.legalToCoordinates = null;

                toCell.setPiece(promotedPiece, fromCell.occupyingPiece.colour);    //sets piece in selected cell
                toCell.legalToCoordinates = toCell.occupyingPiece.getLegalMoves(toCell);

                fromCell.occupyingPiece = null;

                return Colour.NONE;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            fromCell.btn.setIcon(null);   //removes icon from highlightedcell

            //if selected cell is king (some piece removes king), then return colour that won the game
            if(toCell.occupyingPiece != null && toCell.occupyingPiece.name == ChessPiece.KING ) {
                toCell.btn.setIcon(fromCell.occupyingPiece.getImage());
                if(toCell.occupyingPiece.colour == Colour.WHITE)
                    return Colour.BLACK;
                else
                    return Colour.WHITE;

            }

            fromCell.legalToCoordinates = null;

            toCell.setPiece(fromCell.occupyingPiece.name, fromCell.occupyingPiece.colour);    //sets piece in selected cell
            toCell.legalToCoordinates = toCell.occupyingPiece.getLegalMoves(toCell);

            fromCell.occupyingPiece = null;

            return Colour.NONE;
        }


        return Colour.NONE;

    }

    void drawBoardAndAddToPanel(Cell cells[][]) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                cells[row][col] = new Cell(row, col);

                if (cells[row][col].colour == Colour.WHITE) {
                    //White cells will have backgroundColour "SeaShell" to create constrast with White Pieces
                    cells[row][col].setBackgroundColour(255,245,238);
                } else {
                    //Black cells will have backgroundColour "SeaShell" to create constrast with Black Pieces
                    cells[row][col].setBackgroundColour(128,128,0);
                }

                cells[row][col].addToJPanel(panel);
            }

            if (row == 0 || row == 7) {
                setMajorPieces( row);
            } else if (row == 1 || row == 6) {
                setPawns(row);
            }
        }

        wKingCell = cells[7][4];
        bKingCell = cells[0][4];
    }
    void setMajorPieces(int row) {

        Colour c;
        if(row==0) {
            c = Colour.BLACK;
        } else {
            c = Colour.WHITE;
        }

        cells[row][0].setPiece(ChessPiece.ROOK,c);
        cells[row][1].setPiece(ChessPiece.KNIGHT,c);
        cells[row][2].setPiece(ChessPiece.BISHOP,c);

        cells[row][3].setPiece(ChessPiece.QUEEN,c);
        cells[row][4].setPiece(ChessPiece.KING,c);

        cells[row][5].setPiece(ChessPiece.BISHOP,c);
        cells[row][6].setPiece(ChessPiece.KNIGHT,c);
        cells[row][7].setPiece(ChessPiece.ROOK,c);
    }

    void setPawns(int row) {

        if(row==1) {

            //creates 8 black pawns
            for(int i=0 ; i<8 ; i++) {
                cells[row][i].setPiece(ChessPiece.PAWN,Colour.BLACK);
            }


        } else {

            //creates 8 white pawns
            for(int i=0 ; i<8 ; i++) {
                cells[row][i].setPiece(ChessPiece.PAWN,Colour.WHITE);
            }
        }
    }

    static void unHightliteCell(Cell cell) {

        //legal moves' background is removed here
        if(cellSelected.legalToCoordinates != null)
            for(Coordinate coordinates : cellSelected.legalToCoordinates) {
                cells[coordinates.row][coordinates.col].btn.setBorder(BorderFactory.createEmptyBorder());
            }

        btnSelected.setBackground(unselectedColor);
        cellSelected = null;
        btnSelected = null;
        unselectedColor = null;
        hasSelected = false;
    }

    static void highliteCell(Cell cell) {
        //Only highlite cell if it has an occupyingPiece and that Piece matches the colour of the turn player
        if(cell.occupyingPiece != null && matchColour(cell)) {
            cell.legalToCoordinates = cell.occupyingPiece.getLegalMoves(cell);
            cellSelected = cell;
            btnSelected = cell.btn;                                       //Cell's button generated the event.
            unselectedColor = btnSelected.getBackground();
            btnSelected.setBackground(new Color(255, 255, 102));

            hasSelected = true;

            //all legal moves' background is updated here
            if(cell.legalToCoordinates != null)
                for(Coordinate coordinates : cell.legalToCoordinates) {
                    cells[coordinates.row][coordinates.col].btn.setBorder(BorderFactory.createMatteBorder(5,5,5,5,new Color(228, 230, 11)));
                }
            //if he makes a move, change turn
        }
    }

    static boolean matchColour(Cell cell) {
        return turn == cell.occupyingPiece.colour ? true : false;
    }

    public static boolean isEmpty(int row, int col) {
        if(cells[row][col].occupyingPiece == null)
            return true;
        else
            return false;
    }

    public static boolean sameColourPiece(Cell c, int newrow,int newcol) {

        if(isEmpty(newrow,newcol))
            return false;

        Colour originalpiececolour = c.occupyingPiece.colour;
        Colour newpiececolour = cells[newrow][newcol].occupyingPiece.colour;

        if(originalpiececolour == newpiececolour) {
            return true;
        } else {
            return false;
        }
    }

    public static void changeTurn() {
        if(turn == Colour.BLACK)
            turn = Colour.WHITE;
        else
            turn = Colour.BLACK;
    }

    public static ArrayList<Coordinate> isKingAttackedIfPieceRemoved(Cell originalcell) {

        Coordinate path;
        ArrayList<Coordinate> legalcoordinateswhenpinned = new ArrayList<Coordinate>();

        if(originalcell.occupyingPiece.colour == Colour.WHITE) {

            //piece is white
            path = originalcell.subtract(wKingCell);
        } else {
            //piece is black
            path = originalcell.subtract(bKingCell);
        }


        int row = path.row;
        int col = path.col;

        int originalrow = originalcell.getRow();
        int originalcol = originalcell.getCol();
        Colour originalpiececolour = originalcell.getPieceColour();

        if(row == 0 || col == 0 || (abs(row) == abs(col))){

            if(row == 0) {
                if (col < 0) {
                    //(0,-row)
                    for (int j = originalcol+1 ; j < 8; j++) {

                        if (cells[originalrow][j].getPieceColour() == originalpiececolour)
                            return null;
                        if (cells[originalrow][j].getPieceName() == ChessPiece.ROOK || cells[originalrow][j].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalcoordinateswhenpinned.add(new Coordinate(originalrow,j));// attacking piece cell is also legal
                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalcol-1 ; k>=0 ; k--) {
                                if(cells[originalrow][k].getPieceName() == ChessPiece.KING && cells[originalrow][k].getPieceColour() == originalpiececolour)
                                    return legalcoordinateswhenpinned;
                                else{
                                    if(cells[originalrow][k].occupyingPiece != null)
                                        return null;
                                }
                                legalcoordinateswhenpinned.add(new Coordinate(originalrow,k));
                            }
                            return null;
                        }

                        legalcoordinateswhenpinned.add(new Coordinate(originalrow,j));

                    }
                    return null;
                }

                if (col > 0) {
                    for (int j = originalcol-1 ; j >= 0; j--) {

                        if (cells[originalrow][j].getPieceColour() == originalpiececolour)
                            return null;
                        if (cells[originalrow][j].getPieceName() == ChessPiece.ROOK  || cells[originalrow][j].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalcoordinateswhenpinned.add(new Coordinate(originalrow,j));
                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalcol+1 ; k<8 ; k++) {
                                if(cells[originalrow][k].getPieceName() == ChessPiece.KING && cells[originalrow][k].getPieceColour() == originalpiececolour)
                                    return legalcoordinateswhenpinned;
                                else{
                                    if(cells[originalrow][k].occupyingPiece != null)
                                        return null;
                                }
                                legalcoordinateswhenpinned.add(new Coordinate(originalrow,k));
                            }
                            return null;
                        }
                        legalcoordinateswhenpinned.add(new Coordinate(originalrow,j));

                    }
                    return null;
                }
            }

            if(col == 0) {
                if (row < 0) {
                    for (int j = originalrow+1 ; j < 8 ; j++) {

                        if (cells[j][originalcol].getPieceColour() == originalpiececolour)
                            return null;
                        if (cells[j][originalcol].getPieceName() == ChessPiece.ROOK  || cells[j][originalcol].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalcoordinateswhenpinned.add(new Coordinate(j,originalcol));
                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalrow-1 ; k>=0 ; k--) {
                                if(cells[k][originalcol].getPieceName() == ChessPiece.KING && cells[k][originalcol].getPieceColour() == originalpiececolour)
                                    return legalcoordinateswhenpinned;
                                else{
                                    if(cells[k][originalcol].occupyingPiece != null)
                                        return null;
                                }
                                legalcoordinateswhenpinned.add(new Coordinate(k,originalcol));
                            }
                            return null;
                        }

                        legalcoordinateswhenpinned.add(new Coordinate(j,originalcol));
                    }
                    return null;
                }

                if (row > 0) {
                    for (int j = originalrow-1 ; j >= 0 ; j--) {

                        if (cells[j][originalcol].getPieceColour() == originalpiececolour)
                            return null;
                        if (cells[j][originalcol].getPieceName() == ChessPiece.ROOK  || cells[j][originalcol].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalcoordinateswhenpinned.add(new Coordinate(j,originalcol));
                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalrow+1 ; k<8 ; k++) {
                                if(cells[k][originalcol].getPieceName() == ChessPiece.KING && cells[k][originalcol].getPieceColour() == originalpiececolour)
                                    return legalcoordinateswhenpinned;
                                else{
                                    if(cells[k][originalcol].occupyingPiece != null)
                                        return null;
                                }
                                legalcoordinateswhenpinned.add(new Coordinate(k,originalcol));
                            }
                            return null;
                        }
                        legalcoordinateswhenpinned.add(new Coordinate(j,originalcol));
                    }
                    return null;
                }
            }

            //abs(row) == abs(col)

            if(row > col) {
                //(row,-row)

                for(int i=originalrow-1, j=originalcol+1 ; i>=0 && j<8 ; i--,j++) {

                    if(cells[i][j].getPieceColour() == originalpiececolour)
                        return null;
                    if(cells[i][j].getPieceName() == ChessPiece.BISHOP || cells[i][j].getPieceName() ==ChessPiece.QUEEN)
                    {
                        legalcoordinateswhenpinned.add(new Coordinate(i,j));
                        //found a attacking piece
                        //should return true if there is nothing blocking the originalpiece and the king
                        //if there is some piece(any colour) between the originalpiece and the king, then return false

                        for(int k = originalrow+1, l=originalcol-1 ; k<8 && l>=0 ; k++,l--) {
                            if(cells[k][l].getPieceName() == ChessPiece.KING && cells[k][l].getPieceColour() == originalpiececolour)
                                return legalcoordinateswhenpinned;
                            else{
                                if(cells[k][l].occupyingPiece != null)
                                    return null;
                            }
                            legalcoordinateswhenpinned.add(new Coordinate(k,l));
                        }
                        return null;
                    }

                    legalcoordinateswhenpinned.add(new Coordinate(i,j));

                }

                return null;
            } else {
                if(col > row) {
                    //(-row,row)

                    for(int i=originalrow+1, j=originalcol-1 ; i<8 && j>=0 ; i++,j--) {

                        if(cells[i][j].getPieceColour() == originalpiececolour)
                            return null;
                        if(cells[i][j].getPieceName() == ChessPiece.BISHOP || cells[i][j].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalcoordinateswhenpinned.add(new Coordinate(i,j));

                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalrow-1, l=originalcol+1 ; k>=0 && l<8 ; k--,l++) {
                                if(cells[k][l].getPieceName() == ChessPiece.KING && cells[k][l].getPieceColour() == originalpiececolour)
                                    return legalcoordinateswhenpinned;
                                else{
                                    if(cells[k][l].occupyingPiece != null)
                                        return null;
                                }
                                legalcoordinateswhenpinned.add(new Coordinate(k,l));
                            }
                            return null;
                        }
                        legalcoordinateswhenpinned.add(new Coordinate(i,j));
                    }

                    return null;
                } else {
                    //(row,row) or (-row,-row)
                    if(row < 0) {
                        //(-row,-row)

                        for(int i=originalrow+1,j=originalcol+1 ; i<8 && j<8 ; i++,j++) {

                            if(cells[i][j].getPieceColour() == originalpiececolour)
                                return null;
                            if(cells[i][j].getPieceName() == ChessPiece.BISHOP || cells[i][j].getPieceName() == ChessPiece.QUEEN)
                            {
                                legalcoordinateswhenpinned.add(new Coordinate(i,j));
                                //found a attacking piece
                                //should return true if there is nothing blocking the originalpiece and the king
                                //if there is some piece(any colour) between the originalpiece and the king, then return false

                                for(int k = originalrow-1, l=originalcol-1 ; k>=0 && l>=0 ; k--,l--) {
                                    if(cells[k][l].getPieceName() == ChessPiece.KING && cells[k][l].getPieceColour() == originalpiececolour)
                                        return legalcoordinateswhenpinned;
                                    else{
                                        if(cells[k][l].occupyingPiece != null)
                                            return null;
                                    }
                                    legalcoordinateswhenpinned.add(new Coordinate(k,l));
                                }
                                return null;
                            }
                            legalcoordinateswhenpinned.add(new Coordinate(i,j));

                        }
                        return null;

                    } else {
                        //(row,row)

                        for(int i=originalrow-1,j=originalcol-1 ; i>=0 && j>=0 ; i--,j--) {

                            if(cells[i][j].getPieceColour() == originalpiececolour)
                                return null;
                            if(cells[i][j].getPieceName() == ChessPiece.BISHOP || cells[i][j].getPieceName() == ChessPiece.QUEEN)
                            {
                                legalcoordinateswhenpinned.add(new Coordinate(i,j));
                                //found a attacking piece
                                //should return true if there is nothing blocking the originalpiece and the king
                                //if there is some piece(any colour) between the originalpiece and the king, then return false

                                for(int k = originalrow+1, l=originalcol+1 ; k<8 && l<8 ; k++,l++) {
                                    if(cells[k][l].getPieceName() == ChessPiece.KING && cells[k][l].getPieceColour() == originalpiececolour)
                                        return legalcoordinateswhenpinned;
                                    else{
                                        if(cells[k][l].occupyingPiece != null)
                                            return null;
                                    }
                                    legalcoordinateswhenpinned.add(new Coordinate(k,l));
                                }
                                return null;
                            }

                            legalcoordinateswhenpinned.add(new Coordinate(i,j));
                        }
                        return null;

                    }
                }
            }

        } else {
            return null;
        }

    }
}
