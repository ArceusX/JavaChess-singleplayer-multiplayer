package chess.game.network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NetworkDialog {

    JDialog dialog;
    JPanel hostPanel;
    JPanel connectPanel;
    JButton hostBtn;
    JButton connectBtn;
    Host h;
    Connect c;

    public NetworkDialog() {
        dialog = new JDialog();

        dialog.setTitle("Play networked");
        dialog.setLayout(new BoxLayout(dialog.getContentPane(),BoxLayout.Y_AXIS));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModal(true);

        addButtons();

        dialog.pack();
        dialog.setVisible(true);
    }

    private void addButtons() {
        hostPanel = new JPanel();
        connectPanel = new JPanel();

        hostBtn = new JButton("Host");
        connectBtn = new JButton("Connect");

        hostBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                hostSelected();
            }
        });
        connectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                connectSelected();
            }
        });

        hostPanel.add(hostBtn);
        connectPanel.add(connectBtn);

        dialog.add(Box.createRigidArea(new Dimension(250, 50)));
        dialog.add(hostPanel);
        dialog.add(Box.createRigidArea(new Dimension(250, 50)));
        dialog.add(connectPanel);
        dialog.add(Box.createRigidArea(new Dimension(250, 50)));
    }

    private void hostSelected() {
        //Currently the "host", the first player to join the game gets White
        Host h = new Host("Host");             //by default white for now
    }

    private void connectSelected() {
        //Currently the "host", the second player to join the game gets Black
        Connect c = new Connect("Connect");
    }
}
