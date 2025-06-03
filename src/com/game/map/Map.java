package com.game.map;

import java.util.Random;

public class Map {
    private final Tile[][] tiles;
    private final int width;
    private final int height;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];
        initializeTiles();
    }

    private void initializeTiles() {
        Random rand = new Random();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((x == 3 && y == 3) || (x == 5 && y == 5) || (x == 8 && y == 8)) {
                    tiles[x][y] = new Tile(TileType.PLAIN);
                } else {
                    tiles[x][y] = new Tile(TileType.values()[rand.nextInt(3)]);
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile getTile(int x, int y) {

        return tiles[x][y];
    }

    public boolean isValid(int x, int y) {

        return x >= 0 && x < width && y >= 0 && y < height;
    }
}