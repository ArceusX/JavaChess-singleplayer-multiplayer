package chess.game;

import chess.game.logic.LocalGame;
import chess.game.network.NetworkDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Starting point of the program.
 *  Choice of playing:
 *      Locally, in which case, one frame is built for two players to share.
 *
 *      Network, in which case, a frame is built individually for each player.
 *               To activate, select "Play networked" -> "Host", run another
 *               instance of the program, select "Play networked" -> "Connect", type "localhost".
 */
public class Main {

    JFrame frame;
    JButton btnPlayLocal;
    JButton btnPlayNetwork;
    JButton btnExit;

    public Main(String title) {
        frame = new JFrame(title);

        //Boilerplate
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        addButtons();

        btnPlayLocal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new LocalGame("White", "Black");
            }
        });

        btnPlayNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new NetworkDialog();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //https://docs.oracle.com/javase/7/docs/api/javax/swing/JOptionPane.html
                Object[] options = {"Yes", "No"};
                int option = JOptionPane.showOptionDialog(frame, "Are you sure you wish to exit?", "Confirm Exit",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[1]);

                if (option == 0) System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    //Telescoping constructor
    public Main() { this("Chess");}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

    private void addButtons() {

        btnPlayLocal = new JButton("Play locally");
        btnPlayNetwork = new JButton("Play networked");
        btnExit = new JButton("Exit");

        //Create separation between components and center the component vertically
        frame.add(Box.createRigidArea(new Dimension(300, 75)));
        btnPlayLocal.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(btnPlayLocal);

        frame.add(Box.createRigidArea(new Dimension(300, 75)));
        btnPlayNetwork.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(btnPlayNetwork);

        frame.add(Box.createRigidArea(new Dimension(300, 75)));
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(btnExit);
        frame.add(Box.createRigidArea(new Dimension(300, 75)));
    }
}
