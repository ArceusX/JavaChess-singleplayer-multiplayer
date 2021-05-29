package chess.game.network;

import chess.game.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

import static chess.game.Board.updateBoardFromNetwork;
public class Host implements Game {

    String name;
    JDialog boardframe;
    Player player;
    Board board;
    static Colour hostnetworkturn;

    ServerSocket server;
    Socket connection;
    ObjectOutputStream output;
    ObjectInputStream input;

    Host(String hostname) {
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
        createAndShowGUI(hostname);
    }

    private void createAndShowGUI(String hostname) {
        boardframe = new JDialog();
        hostnetworkturn = Colour.WHITE;
        name = hostname;

        boardframe.setTitle("Host");
        boardframe.setSize(800,850);
        boardframe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        boardframe.setModal(true);

        player = new Player(hostname, Colour.WHITE,this);
        //board = new Board(output);
        player.disableDraw();
        boardframe.add(player.playerpanel, BorderLayout.PAGE_END);
//        boardframe.add(board.boardpanel, BorderLayout.CENTER);
        boardframe.setVisible(true);
    }

    public void startRunning() {
        try {
            server = new ServerSocket(6789);

            while (true) {
                createConnection();
                createStreams();
                playGame();
            }

        } catch (IOException e) {
            //i/o error when opening socket
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    public void createConnection() throws IOException{

        String ip = "";

        for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
            final NetworkInterface cur = interfaces.nextElement();

            if (cur.isLoopback()) {
                continue;
            }

            System.out.println("interface " + cur.getName());

            for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
                final InetAddress inet_addr = addr.getAddress();

                if (!(inet_addr instanceof Inet4Address)) {
                    continue;
                }

                ip += inet_addr.getHostAddress() + "\n";
            }
        }

        JOptionPane.showMessageDialog(boardframe,"SERVER IP: " + ip);
        connection = server.accept();
        JOptionPane.showMessageDialog(boardframe,"SERVER: Successfully connected to " + connection.getInetAddress().getHostAddress());
      //  createconn.dispose();

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
                board = new Board(input, output,true);
                boardframe.add(board.panel, BorderLayout.CENTER);
                boardframe.validate();
            }
        });

    }

    public static void modifyHostNetworkTurn(Colour c) {
        hostnetworkturn = c;
    }

    public void playGame() throws IOException {
        ArrayList<Coordinate> movedcells = new ArrayList<Coordinate>();
        do {

           SwingUtilities.isEventDispatchThread();      //no idea why i have to include this line
                                                        //if i remove above line only keep the if condition, everything acts weird

           if(hostnetworkturn == Colour.BLACK) {
                //need to read from client

               //disable the board first
               setEnableRec(boardframe,false);

                try {
                    movedcells = (ArrayList<Coordinate>) input.readObject();
                    updateBoard(movedcells);

                    if(movedcells == null)
                    {
                        JOptionPane.showMessageDialog(boardframe, "You won! Congratulations!");

                        closeConnections();
                        System.exit(0);         //because someone has won, no need to continue the game
                    }

                } catch (ClassNotFoundException classnotfoundexception) {
                    classnotfoundexception.printStackTrace();
                } catch (EOFException eofexception) {

                    closeConnections();
                    System.exit(0);         //because someone has won, no need to continue the game

                }
                //enable the board now, since client has played and the board is updated here
                setEnableRec(boardframe,true);
                //change turncolour back to white
               hostnetworkturn = Colour.WHITE;
            }

        } while(true);    //condition just for now (game is terminated when king is dead)
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
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//will send Host object and receive Connect object, only change board