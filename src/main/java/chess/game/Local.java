package chess.game;

import java.io.Serializable;

/*
 *This class will enable the user to play locally.
 */
public class Local implements Serializable {

    Local() {
        String player1 = "Player1";
        String player2 = "Player2";

        LocalGame newgame = new LocalGame(player1, player2);
    }

}
