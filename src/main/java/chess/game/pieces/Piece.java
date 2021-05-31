package chess.game.pieces;

import chess.game.logic.Cell;
import chess.game.logic.Colour;
import chess.game.logic.Coordinate;
import chess.game.logic.ChessPiece;

import javax.swing.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import static chess.game.logic.Cell.contains;


public abstract class Piece implements Serializable {

    ImageIcon image;
    public ChessPiece name;
    public Colour colour;
    //Set to false when Piece is removed
    boolean isInPlay;

    public abstract ArrayList<Coordinate> getLegalMoves(Cell cell);

    ImageIcon createImageIcon(String path) {

        if(new File(path).exists()) {
            return new ImageIcon(path);
        } else
            return null;
    }

    public ImageIcon getImage() {
        return image;
    }

    public ArrayList<Coordinate> intersection(ArrayList<Coordinate> list1, ArrayList<Coordinate> list2) {

        ArrayList<Coordinate> list = new ArrayList<>();

        for(Coordinate c : list1) {
            if(contains(list2,c)) {
                list.add(c);
            }
        }

        return list;
    }
}
