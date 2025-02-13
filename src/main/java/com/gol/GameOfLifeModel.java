package com.gol;

public class GameOfLifeModel {
    private final int width;
    private final int height;
    private boolean[][] grid;
    private boolean[][] nextGrid;
    private boolean[][] neighborhoodPattern = new boolean[3][3];

    public GameOfLifeModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new boolean[width][height];
        this.nextGrid = new boolean[width][height];
    }

    public void setNeighborhoodPattern(boolean[][] pattern) {
        this.neighborhoodPattern = pattern;
    }

    public void randomize() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[x][y] = Math.random() < 0.2;
            }
        }
    }

    public void updateGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int neighbors = countCustomNeighbors(x, y);
                if (grid[x][y]) {
                    nextGrid[x][y] = (neighbors == 2 || neighbors == 3);
                } else {
                    nextGrid[x][y] = (neighbors == 3);
                }
            }
        }
        boolean[][] temp = grid;
        grid = nextGrid;
        nextGrid = temp;
    }

    private int countCustomNeighbors(int x, int y) {
        int count = 0;
        int[][] directions = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};

        for (int i = 0; i < 8; i++) {
            int nx = x + directions[i][0];
            int ny = y + directions[i][1];
            if (nx >= 0 && ny >= 0 && nx < width && ny < height && grid[nx][ny] && neighborhoodPattern[directions[i][0] + 1][directions[i][1] + 1]) {
                count++;
            }
        }
        return count;
    }

    public boolean getCell(int x, int y) {
        return grid[x][y];
    }

    public void setCell(int x, int y, boolean state) {
        grid[x][y] = state;
    }

    public void clearGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[x][y] = false;
            }
        }
    }
}