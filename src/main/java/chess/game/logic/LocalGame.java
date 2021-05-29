package chess.game.logic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * This class is used to create a chess game.
 * Object of chess.game.logic.LocalGame class will enable you to play chess locally.
 */
public class LocalGame implements Game, Serializable {

    JDialog frame;
    Player player1;
    Player player2;
    Board board;

    public LocalGame(String p1, String p2) {
        frame = new JDialog();

        frame.setTitle("Play chess");
        frame.setSize(800,900);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.setModal(true);

        player1 = new Player(p1, Colour.WHITE, this);
        player2 = new Player(p2,Colour.BLACK, this);

        board = new Board();

        frame.add(player1.playerpanel, BorderLayout.PAGE_END);
        frame.add(board.panel, BorderLayout.CENTER);
        frame.add(player2.playerpanel,BorderLayout.PAGE_START);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String button = actionEvent.getActionCommand();
        StringTokenizer tokens = new StringTokenizer(button);

        String playername = tokens.nextToken();
        String buttonname = tokens.nextToken();

        if(buttonname.equals("Resign")) {

            if(playername.equals(player1.name)) {
                JOptionPane.showMessageDialog(frame,player2.name + " (" + player2.c +") wins!");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(frame,player1.name + " (" + player1.c +") wins!");
                System.exit(0);
            }

        } else {

            //clicked on draw
            Object[] options = {"Yes","No"};
            if(playername.equals(player1.name)) {
                int choice = JOptionPane.showOptionDialog(frame,player1.name + " (" + player1.c +") requests draw. Accept?","Accept Draw?",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
                if(choice == 0) {
                    JOptionPane.showMessageDialog(frame,"Game is draw!");
                    System.exit(0);
                }
            } else {
                int choice = JOptionPane.showOptionDialog(frame,player2.name + " (" + player2.c +") requests draw. Accept?","Accept Draw?",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
                if(choice == 0) {
                    JOptionPane.showMessageDialog(frame,"Game is draw!");
                    System.exit(0);
                }
            }
        }
    }
}
