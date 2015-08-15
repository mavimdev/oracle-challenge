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
    static Cell[][] mapControl;      // to hold the cell info (moves, boxes_done)
    static char[][] mapContent;
    static int initialX; // x coord where the player are at beginning
    static int initialY; // y coord where the player are at beginning
    static int numberBoxes;
    static int minMoves = Integer.MAX_VALUE;
    static String bestPath;


    public static String solve(String mapString) {
        map = mapString;
        fillMatrix();
        findSolution("u", initialX-1, initialY, 1, 0, NO_BOX);
        findSolution("d", initialX+1, initialY, 1, 0, NO_BOX);
        findSolution("l", initialX, initialY-1, 1, 0, NO_BOX);
        findSolution("r", initialX, initialY+1, 1, 0, NO_BOX);
        return bestPath;
    }

    public static void findSolution(String path, int x, int y, int moves, int boxesDone, char pushingBox) {
        boolean stopForward = false;
        // to control where is the box
        int xBox = x;
        int yBox = y;
        char walkedDir;
        // if is in the wall, is a invalid move
        System.out.println(x + "," + y);
        if (pushingBox == NO_BOX && mapContent[x][y] == '#') {
            return;
        }
        // if has more moves than the minimum for this place and for the boxes already accomplished, give up
        if (mapControl[x][y].boxesDone >= boxesDone && mapControl[x][y].moves <= moves) {
            return;
        }
        // getting the last path character - last move done
        // and the box forward coord based on the walk direction
        walkedDir = path.charAt(path.length() - 1);
        switch (walkedDir) {
            case 'u':
                xBox--;
                break;
            case 'd':
                xBox++;
                break;
            case 'l':
                yBox--;
                break;
            case 'r':
                yBox++;
                break;
        }

        // if is pushing a box and the box is in the wall, is a invalid move
        if (pushingBox != NO_BOX) {
            if (mapContent[xBox][yBox] == '#') {
                return;
            }
        }
        // if the new place of the box is the target
        if (mapContent[xBox][yBox] == pushingBox + TO_LOWERCASE) {
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
        // if we have a best result
        if (boxesDone >= mapControl[x][y].boxesDone && moves < mapControl[x][y].moves) {
            mapControl[x][y].boxesDone = boxesDone;
            mapControl[x][y].moves = moves;
        }
        // if there is a box on this cell and is not on target, get this box
        if (mapContent[xBox][yBox] >= 'A'
                && mapContent[xBox][yBox] <= 'Z'
                && mapContent[xBox][yBox] != pushingBox + TO_LOWERCASE) {
            pushingBox = mapContent[xBox][yBox];
        }

        // increments the number of moves for the next step
        moves++;

        // call the recursive function to continuing the search for the best solution
        if(!stopForward || walkedDir != 'u') {
            findSolution(path + "u", x-1, y, moves, boxesDone, pushingBox);
        }
        if(!stopForward || walkedDir != 'd') {
            findSolution(path + "d", x+1, y, moves, boxesDone, pushingBox);
        }
        if(!stopForward || walkedDir != 'l') {
            findSolution(path + "l", x, y-1, moves, boxesDone, pushingBox);
        }
        if(!stopForward || walkedDir != 'r') {
            findSolution(path + "r", x, y+1, moves, boxesDone, pushingBox);
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
        mapControl = new Cell[height][width];
        mapContent = new char[height][width];

        count = 0;
        char charAux;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                charAux = map.charAt(count);
                mapControl[i][j] = new Cell(Integer.MAX_VALUE, 0);
                mapContent[i][j] = charAux;
                if (charAux == '@') {
                    initialX = i;
                    initialY = j;
                    mapControl[i][j].moves = 0;
                    mapControl[i][j].boxesDone = 0;
                }
                // if is a box:
                if (charAux >= 'A' && charAux <= 'Z') {
                    numberBoxes++;
                }
                count++;
            }
            count++;
        }
        //setting free some memory
        map = null;
        if (DEBUG) {
            printMap();
        }
    }

    public static void main(String[] args) {
        String bestPath;
//        bestPath = solve("##########\n# b    B #\n#   @    #\n# A    a #\n#        #\n##########");
        bestPath = solve("######\n#@A a#\n#    #\n######");
        System.out.println(bestPath);
    }

    public static void printMap() {
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("Player initial coords: (" + initialX + "," + initialY + ")");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(mapContent[i][j]);
            }
            System.out.println();
        }

        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mapContent[i][j] == 0) {
                    if(mapControl[i][j].moves == Integer.MAX_VALUE) {
                        System.out.print("!");
                    } else {
                        System.out.print(mapControl[i][j].moves);
                    }
                } else {
                    System.out.print(mapContent[i][j]);
                }
            }
            System.out.println();
        }
    }
}

class Cell {
    public int moves;       // to hold the minimum number of moves to that cell
    public int boxesDone; // to hold how many boxes were done with that number of moves

    public Cell(int moves, int boxesDone) {
        this.moves = moves;
        this.boxesDone = boxesDone;
    }
}