package com.jgegroup.pacman.objects;

import java.util.HashMap;

import com.jgegroup.pacman.Position;
import com.jgegroup.pacman.objects.Enums.*;
import com.jgegroup.pacman.objects.immovable.Tile;


public class MapUtils {
    public static final int tileSize = 32;

    /**
     * @@Authors: Noah, Jesse
     * Gets the surrounding tiles from the map for a given position
     * Throws no Exceptions
     * Returns the surround tiles
     * Takes in board and the position in question
     */
    public static HashMap<Direction, Tile> getSurrounding(HashMap<Position, Tile> map, Position pos) {
        HashMap<Direction, Tile> surrounding = new HashMap<>(8);
        surrounding.put(Direction.UP, map.get(new Position(pos.getX(), pos.getY() + 1)));
        surrounding.put(Direction.LEFT, map.get(new Position(pos.getX() + 1, pos.getY())));
        surrounding.put(Direction.DOWN, map.get(new Position(pos.getX(), pos.getY() - 1)));
        surrounding.put(Direction.RIGHT, map.get(new Position(pos.getX() - 1, pos.getY())));
        return surrounding;
    }
}
