package chess.game.logic;

import java.io.Serializable;
import java.awt.Color;

//Color already used as a class name, so creatively use the British "colour".
public enum Colour implements Serializable {
    WHITE(new Color(255, 245, 238)), BLACK(new Color(128, 128, 0)), NONE(null);

    private final Color backgroundColor;

    public Color getBackgroundColor() {return backgroundColor;}

    Colour(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}