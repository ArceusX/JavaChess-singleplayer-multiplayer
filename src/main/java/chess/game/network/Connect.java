package chess.game.network;

import chess.game.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static chess.game.Board.updateBoardFromNetwork;

//Denotes client class, but as the "Client" name has already been taken...
public class Connect implements Game {

    JDialog boardframe;
    Player player;
    Board board;
    String name;
    public static Colour clientnetworkturn;

    Socket connection;
    ObjectOutputStream output;
    ObjectInputStream input;
    String serverip;

    Connect(String clientname) {

        Thread networkthread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(300);             //start running the server after 300ms, wait for the GUI to load
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startRunning();
            }
        });

        networkthread.start();
        createAndShowGUI(clientname);
    }

    private void createAndShowGUI(String clientname) {

        boardframe = new JDialog();
        clientnetworkturn = Colour.WHITE;                                                                     //turn = white means, its white's turn to play and not turn of client
        name = clientname;

        boardframe.setTitle("Client");
        boardframe.setSize(800,850);
        boardframe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        boardframe.setModal(true);

        player = new Player(clientname, Colour.BLACK,this);                             //this indicates client's piece colour is black
        // board = new Board();
        player.disableDraw();

        boardframe.add(player.playerpanel, BorderLayout.PAGE_START);
        // boardframe.add(board.boardpanel, BorderLayout.CENTER);

        boardframe.setVisible(true);
    }

    private void setEnableRec(final Component container,final boolean enable){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                container.setEnabled(enable);

                try {
                    Component[] components= ((Container) container).getComponents();
                    for (int i = 0; i < components.length; i++) {
                        setEnableRec(components[i], enable);
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startRunning() {
        //since client is always black, we disable the board in the beginning
        //when we receive an object, we change token and then we enable the board
        try {
            createConnection();
            createStreams();
            playGame();

        } catch (IOException e) {
            //i/o error when opening socket
            JOptionPane.showMessageDialog(boardframe,"Connection failed!","Error", JOptionPane.ERROR_MESSAGE);
            boardframe.dispose();
        }
    }

    public static void modifyClientNetworkTurn(Colour c) {
        clientnetworkturn = c;
    }

    public void createConnection() throws IOException {

            serverip = JOptionPane.showInputDialog("Enter the IP address: ");
            connection = new Socket(serverip,6789);
            JOptionPane.showMessageDialog(boardframe,"CLIENT: Successfully connected to " + connection.getInetAddress().getHostAddress());

    }

    public void createStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());

        createBoard();

    }
    private void createBoard() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                board = new Board(input, output,false);
                boardframe.add(board.panel, BorderLayout.CENTER);
                boardframe.validate();
            }
        });

    }

    public void playGame() throws IOException {
        ArrayList<Coordinate> movedcells = new ArrayList<Coordinate>();
        do {
            SwingUtilities.isEventDispatchThread();     //no idea why i have to include this line
                                                        //if i remove above line only keep the if condition, everything acts weird

            if(clientnetworkturn == Colour.WHITE) {
                //need to read from server

                //disable the board first
                setEnableRec(boardframe,false);

                try {

                    movedcells = (ArrayList<Coordinate>) input.readObject();

                    if(movedcells == null)
                    {
                        JOptionPane.showMessageDialog(boardframe, "You won! Congratulations!");

                        closeConnections();
                        System.exit(0);         //because someone has won, no need to continue the game
                    }

                    updateBoard(movedcells);


                } catch (ClassNotFoundException classnotfoundexception) {
                    classnotfoundexception.printStackTrace();
                } catch (EOFException eofexception) {
                    //this is normal execution (game has been terminated on the host side) this happens if host won the game
                    JOptionPane.showMessageDialog(boardframe, "You lost! Please try again!");

                    closeConnections();
                    System.exit(0);         //because someone has won, no need to continue the game

                }
                //enable the board now, since host has played and the board is updated here
                setEnableRec(boardframe,true);
                //change turncolour back to black
                clientnetworkturn = Colour.BLACK;
            }

        } while(true);    //condition just for now
    }

    private void updateBoard(final ArrayList<Coordinate> movedcells) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Coordinate originalcoordinates = movedcells.get(0);
                Coordinate newcoordinates = movedcells.get(1);

                Colour won = updateBoardFromNetwork(originalcoordinates,newcoordinates);

                if(won != Colour.NONE) {

                    if(won == Colour.WHITE)
                        JOptionPane.showMessageDialog(null, "White wins!");
                    else
                        JOptionPane.showMessageDialog(null,"Black wins!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        output.writeObject(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    closeConnections();
                    System.exit(0);         //because someone has won, no need to continue the game
                }
            }
        });
    }

    private void closeConnections() {
        //close streams
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        //what to do when resign and draw is pressed
        String command = actionEvent.getActionCommand();

        if(command.equals(name + " Resign")) {
            int option = JOptionPane.showConfirmDialog(boardframe, "Are you sure you wish to resign the game?", "Confirm Resign", JOptionPane.YES_NO_OPTION);
            if(option == JOptionPane.YES_OPTION) {
                if(output == null) {
                    JOptionPane.showMessageDialog(boardframe, "Not connected to any game!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        output.writeObject(null);
                        JOptionPane.showMessageDialog(boardframe, "You resigned the game!");
                        closeConnections();
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
