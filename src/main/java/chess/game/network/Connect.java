package chess.game.network;

import chess.game.logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static chess.game.logic.Board.updateBoardFromNetwork;

//Denotes client class, but as the "Client" name has already been taken...
public class Connect implements Game {

    JDialog frame;
    Player player;
    Board board;
    String name;
    public static Colour connectTurn;

    Socket socket;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    String serverIp;

    public Connect(String connectname) {

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
        createAndShowGUI(connectname);
    }

    private void createAndShowGUI(String connectname) {

        frame = new JDialog();
        connectTurn = Colour.WHITE;                                                                     //turn = white means, its white's turn to play and not turn of connect
        name = connectname;

        frame.setTitle("Connect");
        frame.setSize(800,850);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setModal(true);

        player = new Player(connectname, Colour.BLACK,this);                             //this indicates connect's piece colour is black
        // board = new Board();
        player.disableDraw();

        frame.add(player.playerpanel, BorderLayout.PAGE_START);

        frame.setVisible(true);
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
        //since connect is always black, we disable the board in the beginning
        //when we receive an object, we change token and then we enable the board
        try {
            createConnection();
            createStreams();
            playGame();

        } catch (IOException e) {
            //i/o error when opening socket
            JOptionPane.showMessageDialog(frame,"Connection failed!","Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        }
    }

    public static void modifyConnectNetworkTurn(Colour c) {
        connectTurn = c;
    }

    public void createConnection() throws IOException {

            serverIp = JOptionPane.showInputDialog("Enter the IP address: ");
            socket = new Socket(serverIp, 1111);
            JOptionPane.showMessageDialog(frame,"CLIENT: Successfully connected to " + socket.getInetAddress().getHostAddress());

    }

    public void createStreams() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());

        createBoard();

    }
    private void createBoard() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                board = new Board(inputStream, outputStream,false);
                frame.add(board.panel, BorderLayout.CENTER);
                frame.validate();
            }
        });

    }

    public void playGame() throws IOException {
        ArrayList<Coordinate> movedcells = new ArrayList<Coordinate>();
        do {
            SwingUtilities.isEventDispatchThread();     //no idea why i have to include this line
                                                        //if i remove above line only keep the if condition, everything acts weird

            if(connectTurn == Colour.WHITE) {
                //need to read from server

                //disable the board first
                setEnableRec(frame,false);

                try {
                    movedcells = (ArrayList<Coordinate>) inputStream.readObject();
                    if(movedcells == null) {
                        JOptionPane.showMessageDialog(frame, "You won! Congratulations!");

                        closeConnections();
                        System.exit(0);         //because someone has won, no need to continue the game
                    }

                    updateBoard(movedcells);


                } catch (ClassNotFoundException classnotfoundexception) {
                    classnotfoundexception.printStackTrace();
                } catch (EOFException eofexception) {
                    //this is normal execution (game has been terminated on the host side) this happens if host won the game
                    JOptionPane.showMessageDialog(frame, "You lost! Fight again!");

                    closeConnections();
                    System.exit(0);         //because someone has won, no need to continue the game

                }
                //enable the board now, since host has played and the board is updated here
                setEnableRec(frame,true);
                //change turncolour back to black
                connectTurn = Colour.BLACK;
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
                        JOptionPane.showMessageDialog(null, "Black wins!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        outputStream.writeObject(null);
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
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        //what to do when resign and draw is pressed
        String command = actionEvent.getActionCommand();

        if(command.equals(name + " Resign")) {
            int option = JOptionPane.showConfirmDialog(frame, "Are you sure you wish to resign the game?", "Confirm Resign", JOptionPane.YES_NO_OPTION);
            if(option == JOptionPane.YES_OPTION) {
                if(outputStream == null) {
                    JOptionPane.showMessageDialog(frame, "Not connected to any game!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        outputStream.writeObject(null);
                        JOptionPane.showMessageDialog(frame, "You resigned the game!");
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
