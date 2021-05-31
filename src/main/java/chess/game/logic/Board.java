package chess.game.logic;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
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
    static Cell whiteKingCell;
    static Cell blackKingCell;

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
    public static void sendMoveOnNetwork(List<Coordinate> movedCells, ChessPiece promotionPiece) {
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

    public static void sendMoveOnNetwork(List<Coordinate> movedCells) {
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

                //White cells have backgroundColour "SeaShell" to create contrast with White Pieces
                //Black cells have backgroundColour "Olive" to create contrast with Black Pieces
                cells[row][col].setBackgroundColor(cells[row][col].colour.getBackgroundColor());
                cells[row][col].addToJPanel(panel);
            }

            if (row == 0 || row == 7) {
                setMajorPieces(row);
            } else if (row == 1 || row == 6) {
                setPawns(row);
            }
        }

        whiteKingCell = cells[7][4];
        blackKingCell = cells[0][4];
    }

    void setMajorPieces(int row) {

        Colour colour = (row == 0) ? Colour.BLACK : Colour.WHITE;

        cells[row][0].setPiece(ChessPiece.ROOK, colour);
        cells[row][1].setPiece(ChessPiece.KNIGHT, colour);
        cells[row][2].setPiece(ChessPiece.BISHOP, colour);

        cells[row][3].setPiece(ChessPiece.QUEEN, colour);
        cells[row][4].setPiece(ChessPiece.KING, colour);

        cells[row][5].setPiece(ChessPiece.BISHOP, colour);
        cells[row][6].setPiece(ChessPiece.KNIGHT, colour);
        cells[row][7].setPiece(ChessPiece.ROOK, colour);
    }

    void setPawns(int row) {
        Colour colour = (row == 1) ? Colour.BLACK : Colour.WHITE;
        //creates 8 black pawns
        for(int i=0 ; i<8 ; i++) {
            cells[row][i].setPiece(ChessPiece.PAWN, colour);
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
        if(cell.occupyingPiece != null && matchTurn(cell)) {
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

    static boolean matchTurn(Cell cell) {
        return (cell.occupyingPiece.colour == turn) ? true : false;
    }

    public static boolean isEmpty(int row, int col) {
        return (cells[row][col].occupyingPiece == null) ? true : false;
    }

    public static boolean isMatchedColour(Cell fromCell, int toRow, int toCol) {

        if(isEmpty(toRow, toCol)) {
            return false;
        }

        return (fromCell.occupyingPiece.colour == cells[toRow][toCol].occupyingPiece.colour) ?
                true : false;
    }

    public static void passTurn() {
        turn = (turn == Colour.BLACK) ? Colour.WHITE : Colour.BLACK;
    }

    //if isKingAttackedIfPieceRemoved(..) is true, that piece is pinned to
    //its same coloured King and cannot move because a move would put its King in check.
    public static List<Coordinate> isKingAttackedIfPieceRemoved(Cell fromCell) {

        List<Coordinate> legalCoordinatesOnPin = new ArrayList<>();

        Coordinate path = (fromCell.occupyingPiece.colour == Colour.WHITE) ?
                fromCell.subtract(whiteKingCell) : fromCell.subtract(blackKingCell);

        int rowDiff = path.row;
        int colDiff = path.col;

        int originalRow = fromCell.getRow();
        int originalCol = fromCell.getCol();
        Colour originalPieceColour = fromCell.getPieceColour();

        /* The piece occupying fromCell, is on and block an enemy piece's attack
         * on that occupyingPiece's same colored King, on the same, respectively,
         *      row (rowDiff == 0),
         *      col (colDiff == 0),
         *      diagonal (abs(rowDiff) == abs(colDiff)).
         *
         * e.g. A piece sharing a row with its King can potentially be blocking a
         *      an attack from an enemy Rook or Queen.
         */
        if (rowDiff == 0 || colDiff == 0 || (abs(rowDiff) == abs(colDiff))) {

            if(rowDiff == 0) {
                if (colDiff < 0) {
                    //(0,-rowDiff)
                    for (int j = originalCol+1 ; j < 8; j++) {

                        if (cells[originalRow][j].getPieceColour() == originalPieceColour)
                            return null;
                        if (cells[originalRow][j].getPieceName() == ChessPiece.ROOK || cells[originalRow][j].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalCoordinatesOnPin.add(new Coordinate(originalRow,j));// attacking piece cell is also legal
                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalCol-1 ; k>=0 ; k--) {
                                if(cells[originalRow][k].getPieceName() == ChessPiece.KING && cells[originalRow][k].getPieceColour() == originalPieceColour)
                                    return legalCoordinatesOnPin;
                                else{
                                    if(cells[originalRow][k].occupyingPiece != null)
                                        return null;
                                }
                                legalCoordinatesOnPin.add(new Coordinate(originalRow,k));
                            }
                            return null;
                        }

                        legalCoordinatesOnPin.add(new Coordinate(originalRow,j));

                    }
                    return null;
                }

                if (colDiff > 0) {
                    for (int j = originalCol-1 ; j >= 0; j--) {

                        if (cells[originalRow][j].getPieceColour() == originalPieceColour)
                            return null;
                        if (cells[originalRow][j].getPieceName() == ChessPiece.ROOK  || cells[originalRow][j].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalCoordinatesOnPin.add(new Coordinate(originalRow,j));
                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalCol+1 ; k<8 ; k++) {
                                if(cells[originalRow][k].getPieceName() == ChessPiece.KING && cells[originalRow][k].getPieceColour() == originalPieceColour)
                                    return legalCoordinatesOnPin;
                                else{
                                    if(cells[originalRow][k].occupyingPiece != null)
                                        return null;
                                }
                                legalCoordinatesOnPin.add(new Coordinate(originalRow,k));
                            }
                            return null;
                        }
                        legalCoordinatesOnPin.add(new Coordinate(originalRow,j));

                    }
                    return null;
                }
            }

            if(colDiff == 0) {
                if (rowDiff < 0) {
                    for (int j = originalRow+1 ; j < 8 ; j++) {

                        if (cells[j][originalCol].getPieceColour() == originalPieceColour)
                            return null;
                        if (cells[j][originalCol].getPieceName() == ChessPiece.ROOK  || cells[j][originalCol].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalCoordinatesOnPin.add(new Coordinate(j,originalCol));
                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalRow-1 ; k>=0 ; k--) {
                                if(cells[k][originalCol].getPieceName() == ChessPiece.KING && cells[k][originalCol].getPieceColour() == originalPieceColour)
                                    return legalCoordinatesOnPin;
                                else{
                                    if(cells[k][originalCol].occupyingPiece != null)
                                        return null;
                                }
                                legalCoordinatesOnPin.add(new Coordinate(k,originalCol));
                            }
                            return null;
                        }

                        legalCoordinatesOnPin.add(new Coordinate(j,originalCol));
                    }
                    return null;
                }

                if (rowDiff > 0) {
                    for (int j = originalRow-1 ; j >= 0 ; j--) {

                        if (cells[j][originalCol].getPieceColour() == originalPieceColour)
                            return null;
                        if (cells[j][originalCol].getPieceName() == ChessPiece.ROOK  || cells[j][originalCol].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalCoordinatesOnPin.add(new Coordinate(j,originalCol));
                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalRow+1 ; k<8 ; k++) {
                                if(cells[k][originalCol].getPieceName() == ChessPiece.KING && cells[k][originalCol].getPieceColour() == originalPieceColour)
                                    return legalCoordinatesOnPin;
                                else{
                                    if(cells[k][originalCol].occupyingPiece != null)
                                        return null;
                                }
                                legalCoordinatesOnPin.add(new Coordinate(k,originalCol));
                            }
                            return null;
                        }
                        legalCoordinatesOnPin.add(new Coordinate(j,originalCol));
                    }
                    return null;
                }
            }

            //abs(rowDiff) == abs(colDiff)

            if(rowDiff > colDiff) {
                //(rowDiff,-rowDiff)

                for(int i=originalRow-1, j=originalCol+1 ; i>=0 && j<8 ; i--,j++) {

                    if(cells[i][j].getPieceColour() == originalPieceColour)
                        return null;
                    if(cells[i][j].getPieceName() == ChessPiece.BISHOP || cells[i][j].getPieceName() ==ChessPiece.QUEEN)
                    {
                        legalCoordinatesOnPin.add(new Coordinate(i,j));
                        //found a attacking piece
                        //should return true if there is nothing blocking the originalpiece and the king
                        //if there is some piece(any colour) between the originalpiece and the king, then return false

                        for(int k = originalRow+1, l=originalCol-1 ; k<8 && l>=0 ; k++,l--) {
                            if(cells[k][l].getPieceName() == ChessPiece.KING && cells[k][l].getPieceColour() == originalPieceColour)
                                return legalCoordinatesOnPin;
                            else{
                                if(cells[k][l].occupyingPiece != null)
                                    return null;
                            }
                            legalCoordinatesOnPin.add(new Coordinate(k,l));
                        }
                        return null;
                    }

                    legalCoordinatesOnPin.add(new Coordinate(i,j));

                }

                return null;
            } else {
                if(colDiff > rowDiff) {
                    //(-rowDiff,rowDiff)

                    for(int i=originalRow+1, j=originalCol-1 ; i<8 && j>=0 ; i++,j--) {

                        if(cells[i][j].getPieceColour() == originalPieceColour)
                            return null;
                        if(cells[i][j].getPieceName() == ChessPiece.BISHOP || cells[i][j].getPieceName() == ChessPiece.QUEEN)
                        {
                            legalCoordinatesOnPin.add(new Coordinate(i,j));

                            //found a attacking piece
                            //should return true if there is nothing blocking the originalpiece and the king
                            //if there is some piece(any colour) between the originalpiece and the king, then return false

                            for(int k = originalRow-1, l=originalCol+1 ; k>=0 && l<8 ; k--,l++) {
                                if(cells[k][l].getPieceName() == ChessPiece.KING && cells[k][l].getPieceColour() == originalPieceColour)
                                    return legalCoordinatesOnPin;
                                else{
                                    if(cells[k][l].occupyingPiece != null)
                                        return null;
                                }
                                legalCoordinatesOnPin.add(new Coordinate(k,l));
                            }
                            return null;
                        }
                        legalCoordinatesOnPin.add(new Coordinate(i,j));
                    }

                    return null;
                } else {
                    //(rowDiff,rowDiff) or (-rowDiff,-rowDiff)
                    if(rowDiff < 0) {
                        //(-rowDiff,-rowDiff)

                        for(int i=originalRow+1,j=originalCol+1 ; i<8 && j<8 ; i++,j++) {

                            if(cells[i][j].getPieceColour() == originalPieceColour)
                                return null;
                            if(cells[i][j].getPieceName() == ChessPiece.BISHOP || cells[i][j].getPieceName() == ChessPiece.QUEEN)
                            {
                                legalCoordinatesOnPin.add(new Coordinate(i,j));
                                //found a attacking piece
                                //should return true if there is nothing blocking the originalpiece and the king
                                //if there is some piece(any colour) between the originalpiece and the king, then return false

                                for(int k = originalRow-1, l=originalCol-1 ; k>=0 && l>=0 ; k--,l--) {
                                    if(cells[k][l].getPieceName() == ChessPiece.KING && cells[k][l].getPieceColour() == originalPieceColour)
                                        return legalCoordinatesOnPin;
                                    else{
                                        if(cells[k][l].occupyingPiece != null)
                                            return null;
                                    }
                                    legalCoordinatesOnPin.add(new Coordinate(k,l));
                                }
                                return null;
                            }
                            legalCoordinatesOnPin.add(new Coordinate(i,j));

                        }
                        return null;

                    } else {
                        //(rowDiff,rowDiff)

                        for(int i=originalRow-1,j=originalCol-1 ; i>=0 && j>=0 ; i--,j--) {

                            if(cells[i][j].getPieceColour() == originalPieceColour)
                                return null;
                            if(cells[i][j].getPieceName() == ChessPiece.BISHOP || cells[i][j].getPieceName() == ChessPiece.QUEEN)
                            {
                                legalCoordinatesOnPin.add(new Coordinate(i,j));
                                //found a attacking piece
                                //should return true if there is nothing blocking the originalpiece and the king
                                //if there is some piece(any colour) between the originalpiece and the king, then return false

                                for(int k = originalRow+1, l=originalCol+1 ; k<8 && l<8 ; k++,l++) {
                                    if(cells[k][l].getPieceName() == ChessPiece.KING && cells[k][l].getPieceColour() == originalPieceColour)
                                        return legalCoordinatesOnPin;
                                    else{
                                        if(cells[k][l].occupyingPiece != null)
                                            return null;
                                    }
                                    legalCoordinatesOnPin.add(new Coordinate(k,l));
                                }
                                return null;
                            }

                            legalCoordinatesOnPin.add(new Coordinate(i,j));
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
