package chess.game.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface Game extends ActionListener {
    @Override
    void actionPerformed(ActionEvent actionEvent);
}
