package com.oracle.challenge;

/**
 * Created by Miguel on 04/07/2015.
 */
public class BoxSolver {
    static final Boolean DEBUG = false;
    static final char TO_LOWERCASE = 32;
    static final char NO_BOX = '_';
    static String map;
    static int width, height;
    static Cell[][] mapMatrix;      // to hold the content of the cell and  related info (content, moves, boxes_done)
    static int initialX; // x coord where the player are at beginning
    static int initialY; // y coord where the player are at beginning
    static int numberBoxes;
    static int minMoves = Integer.MAX_VALUE;
    static String bestPath;


    public static String solve(String problem) {
        map = problem;
        fillMatrix();
        findSolution("u", initialX, initialY-1, 1, 0, NO_BOX);
        findSolution("d", initialX, initialY+1, 1, 0, NO_BOX);
        findSolution("l", initialX-1, initialY, 1, 0, NO_BOX);
        findSolution("r", initialX+1, initialY, 1, 0, NO_BOX);
        return bestPath;
    }

    public static void findSolution(String path, int x, int y, int moves, int boxesDone, char pushingBox) {
        boolean stopForward = false;
        int forwardX = x;
        int forwardY = y;
        char walkedDir;
        // if is in the wall, is a invalid move
        System.out.println(x + "," + y);
        if (pushingBox == NO_BOX && mapMatrix[x][y].content == '#') {
            return;
        }
        // getting the last path character - last move done
        // and the box forward coord based on the walk direction
        walkedDir = path.charAt(path.length() - 1);
        switch (walkedDir) {
            case 'u':
                forwardY--;
                break;
            case 'd':
                forwardY++;
                break;
            case 'l':
                forwardX--;
                break;
            case 'r':
                forwardX++;
                break;
        }

        // if is pushing a box and the box is in the wall, is a invalid move
        if (pushingBox != NO_BOX) {
            if (mapMatrix[forwardX][forwardY].content == '#') {
                return;
            }
        }

        if (mapMatrix[forwardX][forwardY].content == pushingBox + TO_LOWERCASE) {
            boxesDone++;
            pushingBox = NO_BOX;
            stopForward = true;
            if (boxesDone == numberBoxes) {
                if (moves < minMoves) {
                    minMoves = moves;
                    bestPath = path;
                }
                return;
            }
        }

        // if has more moves than the minimum for this place and for the boxes already accomplished, give up
        if (mapMatrix[x][y].boxesDone >= boxesDone && mapMatrix[x][y].moves <= moves) {
            return;
        }
        if (boxesDone >= mapMatrix[x][y].boxesDone && moves < mapMatrix[x][y].moves) {
            mapMatrix[x][y].boxesDone = boxesDone;
            mapMatrix[x][y].moves = moves;
        }

        if (mapMatrix[forwardX][forwardY].content >= 'A'
                && mapMatrix[forwardX][forwardY].content <= 'Z'
                && mapMatrix[forwardX][forwardY].content != pushingBox + TO_LOWERCASE) {
            pushingBox = mapMatrix[forwardX][forwardY].content;
        }

        // increments the number of moves for the next step
        moves++;

        // call the recursive function to continuing the search for the best solution
        if(!stopForward || walkedDir != 'u') {
            findSolution(path + "u", x, y-1, moves, boxesDone, pushingBox);
        }
        if(!stopForward || walkedDir != 'd') {
            findSolution(path + "d", x, y+1, moves, boxesDone, pushingBox);
        }
        if(!stopForward || walkedDir != 'l') {
            findSolution(path + "l", x-1, y, moves, boxesDone, pushingBox);
        }
        if(!stopForward || walkedDir != 'r') {
            findSolution(path + "r", x+1, y, moves, boxesDone, pushingBox);
        }
    }

    public static void fillMatrix() {
        int count = 0;
        for (int i = 0; i < map.length(); i++) {
            if (map.charAt(i) == '\n') {
                count++;
            }
        }
        width = map.indexOf('\n');
        height = ++count;
        mapMatrix = new Cell[height][width];

        count = 0;
        char charAux;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                charAux = map.charAt(count);
                mapMatrix[i][j] = new Cell(charAux, Integer.MAX_VALUE, 0);
                if (charAux == '@') {
                    initialX = i;
                    initialY = j;
                }
                // if is a box:
                if (charAux >= 'A' && charAux <= 'Z') {
                    numberBoxes++;
                }
                count++;
            }
            count++;
        }
        //freeing some memory
        map = null;
    }

    public static void main(String[] args) {
        String bestPath;
        bestPath = solve("##########\n# b    B #\n#   @    #\n# A    a #\n#        #\n##########");
        System.out.println(bestPath);

        if (DEBUG) {
            printMap();
        }
    }

    public static void printMap() {
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Player initial coords: (" + initialX + "," + initialY + ")");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(mapMatrix[i][j].content);
            }
            System.out.println();
        }

        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mapMatrix[i][j].content != '#' && mapMatrix[i][j].content < 64) {
                    System.out.print(mapMatrix[i][j].moves);
                } else {
                    System.out.print(mapMatrix[i][j].content);
                }
            }
            System.out.println();
        }
    }
}

class Cell {
    public char content;    // to hold the content of the cell
    public int moves;       // to hold the minimum number of moves to that cell
    public int boxesDone; // to hold how many boxes were done with that number of moves

    public Cell(char content, int moves, int boxesDone) {
        this.content = content;
        this.moves = moves;
        this.boxesDone = boxesDone;
    }
}