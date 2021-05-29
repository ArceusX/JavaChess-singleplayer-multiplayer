package chess.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * This class is used to create a chess game.
 * Object of chess.nmamit.LocalGame class will enable you to play chess locally.
 */
public class LocalGame implements Game, Serializable {

    JDialog boardframe;
    Player player1;
    Player player2;
    Board board;

    LocalGame(String p1, String p2) {
        boardframe = new JDialog();

        boardframe.setTitle("Play chess");
        boardframe.setSize(800,900);
        boardframe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        boardframe.setModal(true);

        player1 = new Player(p1, Colour.WHITE, this);
        player2 = new Player(p2,Colour.BLACK, this);

        board = new Board();

        boardframe.add(player1.playerpanel, BorderLayout.PAGE_END);
        boardframe.add(board.panel, BorderLayout.CENTER);
        boardframe.add(player2.playerpanel,BorderLayout.PAGE_START);

        boardframe.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String button = actionEvent.getActionCommand();
        StringTokenizer tokens = new StringTokenizer(button);

        String playername = tokens.nextToken();
        String buttonname = tokens.nextToken();

        if(buttonname.equals("Resign")) {

            if(playername.equals(player1.name)) {
                JOptionPane.showMessageDialog(boardframe,player2.name + " (" + player2.c +") wins!");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(boardframe,player1.name + " (" + player1.c +") wins!");
                System.exit(0);
            }

        } else {

            //clicked on draw
            Object[] options = {"Yes","No"};
            if(playername.equals(player1.name)) {
                int choice = JOptionPane.showOptionDialog(boardframe,player1.name + " (" + player1.c +") requests draw. Accept?","Accept Draw?",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
                if(choice == 0) {
                    JOptionPane.showMessageDialog(boardframe,"Game is draw!");
                    System.exit(0);
                }
            } else {
                int choice = JOptionPane.showOptionDialog(boardframe,player2.name + " (" + player2.c +") requests draw. Accept?","Accept Draw?",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
                if(choice == 0) {
                    JOptionPane.showMessageDialog(boardframe,"Game is draw!");
                    System.exit(0);
                }
            }



        }


    }
}
