package chess.game;

import chess.game.network.NetworkGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Starting point of the program.
 *  Choice of playing:
 *      locally, in which case, one frame is built for two players to share
 *
 *      network, in which case, a frame is built individually for each player.
 *               To activate, select "Play networked" -> "Host",
 *               run another instance, select "Play networked" -> "Client", type "localhost"
 */
public class Main {

    JFrame frame;
    JPanel panelLocal;
    JPanel panelNetwork;
    JPanel panelExit;
    JButton buttonPlayLocal;
    JButton buttonPlayNetwork;
    JButton buttonExit;

    Main() {
        frame = new JFrame("Chess");

        //Boilerplate
        //frame.setSize(1000,1000);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        addButtons();

        buttonPlayLocal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new Local();
            }
        });

        buttonPlayNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new NetworkGame();
            }
        });

        buttonExit.addActionListener(new ActionListener() {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

    private void addButtons() {

        panelLocal = new JPanel();
        panelNetwork = new JPanel();
        panelExit = new JPanel();

        buttonPlayLocal = new JButton("Play locally");
        buttonPlayNetwork = new JButton("Play networked");
        buttonExit = new JButton("Exit");

        panelLocal.add(buttonPlayLocal);
        panelNetwork.add(buttonPlayNetwork);
        panelExit.add(buttonExit);

        frame.add(Box.createRigidArea(new Dimension(250, 50)));
        frame.add(panelLocal);
        frame.add(Box.createRigidArea(new Dimension(250, 50)));
        frame.add(panelNetwork);
        frame.add(Box.createRigidArea(new Dimension(250, 50)));
        frame.add(panelExit);
        frame.add(Box.createRigidArea(new Dimension(250, 50)));
    }
}
