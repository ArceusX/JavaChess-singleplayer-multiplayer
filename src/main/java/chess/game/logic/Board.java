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

    /*if isKingCheckedIfPieceRemoved(..) is true, that piece is pinned to its
      same coloured King and cannot move to a cell not along that pin path
      because doing so would put its King in check. */
    public static List<Coordinate> isKingCheckedIfPieceRemoved(Cell fromCell) {

        List<Coordinate> legalCoordinatesWithPin = new ArrayList<>();

        Coordinate path = ((fromCell.occupyingPiece.colour == Colour.WHITE)
                ? whiteKingCell : blackKingCell).subtract(fromCell);

        int rowDifference = path.row;
        int colDifference = path.col;

        int fromRow = fromCell.getRow();
        int fromCol = fromCell.getCol();

        int colShiftDir;
        int rowShiftDir;
        int endForward;
        int endBackward;

        /* fromCell's occupyingPiece, is on and block an enemy piece's attack
         * on that occupyingPiece's same colored King, on the same, respectively,
         *      row (rowDifference == 0),
         *      col (colDifference == 0),
         *      diagonal (abs(rowDifference) == abs(colDifference)).
         *
         * e.g. fromCell's occupyingPiece sharing a row with its King can potentially
         *      be blocking and thus is pinned by an attack from an enemy Rook or Queen.
         */
        if (rowDifference == 0 || colDifference == 0 || abs(rowDifference) == abs(colDifference)) {

            /*Record, along with Queen, whether also need to check
              if enemy piece is Rook or Bishop, respectively. */
            boolean isCheckingDiagonal;

            if (rowDifference == 0) {
                isCheckingDiagonal = false;

                /*e.g. Case of King is east of fromCell's occupyingPiece
                       (colDifference = whiteKingCell.subtract(fromCell)).col = colDifference < 0,
                       so King is on the same row, but on a lower col to fromCell's occupiedPiece,
                       so check if there's any enemy piece in the opposite direction:
                       with higher col, up to col further in that opposite direction (ie. 8)
                */

                rowShiftDir = 0;
                colShiftDir = (colDifference < 0) ? 1 : -1;

                endForward = (colDifference < 0) ? 8 : -1;
                endBackward = (colDifference < 0) ? -1 : 8;
            } else if (colDifference == 0) {
                isCheckingDiagonal = false;

                colShiftDir = 0;
                rowShiftDir = (rowDifference < 0) ? 1 : -1;

                endForward = (rowDifference < 0) ? 8 : -1;
                endBackward = (rowDifference < 0) ? -1 : 8;
            }
            else {
                isCheckingDiagonal = true;

                /*e.g. Case (1 of 4 possible) of King in
                  (rowDifference is +, colDifference is +)/lower-right half-diagonal,
                  so pinning threat may originate in its reflection being
                  (rowShiftDir is -, colShiftDir is -)/upper-left half-diagonal.*/

                rowShiftDir = (rowDifference > 0) ? -1 : 1;
                colShiftDir = (colDifference > 0) ? -1 : 1;

                //e.g. King is on higher row, so need to check to row -1 for threat from enemy
                endForward = (Integer.signum(rowDifference) == 1) ? -1 : 8;
                endBackward = (Integer.signum(rowDifference) == 1) ? 8 : -1;
            }

            int rowChecked = fromRow + rowShiftDir;
            int colChecked = fromCol + colShiftDir;

            for (; (rowChecked != endForward) && (colChecked > -1) && (colChecked < 8);
                 rowChecked = rowChecked + rowShiftDir, colChecked = colChecked + colShiftDir) {

                /*Threat cannot originate from a piece of the same colour. And that piece performs
                  the block if one is necessary, so can end the need to check further. */
                if (cells[rowChecked][colChecked].getPieceColour() == fromCell.getPieceColour()) {
                    return null;
                }

                /*If enemy piece is verified later to be pinning, can resolve that pin by moving
                  forward along the pin path, including possibly capturing pinning enemy piece */
                legalCoordinatesWithPin.add(new Coordinate(rowChecked, colChecked));

                if (cells[rowChecked][colChecked].getPieceName() == ChessPiece.QUEEN ||
                        (isCheckingDiagonal && cells[rowChecked][colChecked].getPieceName() == ChessPiece.BISHOP) ||
                        (!isCheckingDiagonal && cells[rowChecked][colChecked].getPieceName() == ChessPiece.ROOK)) {

                    int rowBlocked = fromRow - rowShiftDir;
                    int colBlocked = fromCol - colShiftDir;

                    for (; (rowChecked != endBackward) && (colChecked > -1) && (colChecked < 8);
                         rowBlocked = rowBlocked - rowShiftDir, colBlocked = colBlocked - colShiftDir) {

                        //Blocked cell contains the same coloured King
                        if (cells[rowBlocked][colBlocked].getPieceName() == ChessPiece.KING &&
                                cells[rowBlocked][colBlocked].getPieceColour() == fromCell.getPieceColour()) {
                            return legalCoordinatesWithPin;
                        }

                        /*Another piece, of either colour, is already blocking, so
                          fromCell's occupiedPiece doesn't need to. */
                        else if (cells[rowBlocked][colBlocked].occupyingPiece != null) {
                            return null;
                        }

                        //Can block by moving to a cell backward along that pin path
                        legalCoordinatesWithPin.add(new Coordinate(rowBlocked, colBlocked));
                    }

                    /*If preceding for-loop is skipped, it means fromCell's occupiedPiece cannot move
                      backward, so there cannot exist any cell for it to potentially block. */
                    return null;
                }
            }

            //No piece exists that is pinning along the row or col or diagonal
            return null;
        }
        else {
            return null;
        }
    }
}
