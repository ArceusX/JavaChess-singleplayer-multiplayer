package chess.game.network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NetworkGame {

    JDialog networkdialog;
    JPanel hostPanel;
    JPanel clientPanel;
    JButton hostButton;
    JButton clientButton;
    Host h;
    Connect c;

    public NetworkGame() {
        networkdialog = new JDialog();

        networkdialog.setTitle("Play networked");
        networkdialog.setLayout(new BoxLayout(networkdialog.getContentPane(),BoxLayout.Y_AXIS));
        networkdialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        networkdialog.setModal(true);

        addButtons();


        networkdialog.pack();
        networkdialog.setVisible(true);
    }

    private void addButtons() {
        hostPanel = new JPanel();
        clientPanel = new JPanel();

        hostButton = new JButton("Host");
        clientButton = new JButton("Client");

        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                hostSelected();
            }
        });
        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clientSelected();
            }
        });

        hostPanel.add(hostButton);
        clientPanel.add(clientButton);

        networkdialog.add(Box.createRigidArea(new Dimension(250, 50)));
        networkdialog.add(hostPanel);
        networkdialog.add(Box.createRigidArea(new Dimension(250, 50)));
        networkdialog.add(clientPanel);
        networkdialog.add(Box.createRigidArea(new Dimension(250, 50)));
    }

    private void hostSelected() {
        Host h = new Host("Host");             //by default white for now
    }

    private void clientSelected() {
        Connect c = new Connect("Client");        //by default black for now
    }
}
