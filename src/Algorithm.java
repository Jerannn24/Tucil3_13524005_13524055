import java.util.*;

import java.io.*;

public class Algorithm{
    private Map map;
    private int minCost = Integer.MAX_VALUE;
    private int totalIteration = 0;
    private String iterationFilePath = "iteration.txt";
    private String heuristicType = "H1";
    private int[] dx = {-1,1,0,0};
    private int[] dy = {0,0,-1,1};
    private char[] dir = {'U', 'D', 'L', 'R'};

    public Algorithm(Map map){
        this.map = map;
        int[][] costs = map.getBoardCosts();
        for (int i=0; i<map.getRowCount(); i++){
            for (int j = 0; j < map.getColCount(); j++){
                if (costs[i][j] < minCost && costs[i][j] >0){
                    minCost= costs[i][j];
                }
            }
        }
    }

    public void setIterationFilePath(String iterationFilePath){
        if (iterationFilePath != null && !iterationFilePath.isEmpty()){
            this.iterationFilePath = iterationFilePath;
        }
    }

    public void setHeuristicType(String heuristicType){
        if (heuristicType != null && !heuristicType.isEmpty()){
            this.heuristicType = heuristicType;
        }
    }
    private int heuristicCost(int x, int y, int targetDigit, String algorithm){
        if (algorithm.equals("UCS")) return 0;
        int targetX = map.getTargetX();
        int targetY = map.getTargetY();

        if (targetDigit <= map.getMaxDigit()){
            char[][] board = map.getBoard();
            for (int i = 0; i < map.getRowCount(); i++){
                for (int j = 0; j < map.getColCount(); j++){
                    if (board[i][j] >= '0' && board[i][j] <= '9') {
                        int digit = board[i][j] - '0'; 
                        if (digit == targetDigit) {
                            targetX = i; 
                            targetY = j; 
                            break;
                        }
                    }
                }
            }
        }
        int manhattan = Math.abs(x-targetX) + Math.abs(y-targetY);
        switch (heuristicType){
            case "H2":
                return manhattan * minCost; // INI NANTI AJA PAS BIKIN BONUS
            case "H3":
                return (manhattan + Math.abs(targetX - map.getTargetX()) + Math.abs(targetY - map.getTargetY())) * minCost; 
            case "H1":
            default:
                return manhattan * minCost;
        }
    }

    private State slide(State cur, int dirX, int dirY, char dir, String algorithm, PrintWriter wr){
        int x= cur.getX();
        int y = cur.getY();
        int costStep = 0;
        int curTarget = cur.getCurDigit();
        boolean moved = false;
        char[][] board = map.getBoard();
        int[][] costs = map.getBoardCosts();
        int prevTarget = curTarget;
        while (true){
            int newX= x + dirX;
            int newY = y +dirY;
            if (newX < 0 || newX >= map.getRowCount() || newY < 0 || newY >= map.getColCount()){
                wr.println("   -> " + dir + " GAME OVER (Keluar dari batas papan)!");
                return null;
            }
            char tile = board[newX][newY];
            if (tile == 'X') break;
            if (tile == 'L') {
                wr.println("   -> " + dir + " GAME OVER (Melewati Lava)!");
                return null;
            }
            x = newX;
            y = newY;
            costStep += costs[x][y];
            moved = true;

            if (Character.isDigit(tile)){
                int digit = Character.getNumericValue(tile);
                if (digit == curTarget){
                    curTarget++;
                } else if(digit > curTarget){
                    wr.println("   -> " + dir + " GAME OVER (Angka tidak sesuai urutan)");
                    return null;
                }
            }
        }
        if (!moved) return null;
        int newG = (cur.getG()) + costStep;
        int newH = 0;
        int newF = newG;
        
        if (algorithm.equals("BFS") || algorithm.equals("DFS")){
            newF = newG;
        } else {
            newH = heuristicCost(x, y, curTarget, algorithm);
            newF = (algorithm.equals("GBFS")) ? newH: (newG + newH);
        }

        boolean isTargetFulfilled = (curTarget > prevTarget);
        return new State(x, y, newG, newF, cur.getSteps() + dir, curTarget, isTargetFulfilled);

    }

    public State solve(String algorithm){
        if (algorithm.equals("BFS")){
            return solveBFS();
        } else if (algorithm.equals("DFS")){
            return solveDFS();
        } else {
            return solveBestFirst(algorithm);
        }
    }

    private State solveBestFirst(String algorithm){
        PriorityQueue<State> queue = new PriorityQueue<>();
        boolean[][][] visited = new boolean[map.getRowCount()][map.getColCount()][11];

        try (PrintWriter wr =new PrintWriter(new FileWriter(iterationFilePath))){
            State start = new State(map.getInitialX(), map.getInitialY(), 0, 0, "", 0, false);
            queue.add(start);

            while(!queue.isEmpty()){
                State cur = queue.poll();
                totalIteration++;
                wr.println("=== Iteration " + totalIteration + " ===");
                wr.println("Initial");
                for (char[] row : loadSteps(0, cur.getSteps())){
                    wr.println(new String(row));
                }
                wr.println();
                for (int i = 1; i <= cur.getSteps().length(); i++){
                    wr.println("Step " + i + " : " + cur.getSteps().charAt(i-1));
                    for (char[] row : loadSteps(i, cur.getSteps())){
                        wr.println(new String(row));
                    }
                    wr.println();
                }

                if (map.getBoard()[cur.getX()][cur.getY()] == 'O' && cur.getCurDigit() > map.getMaxDigit()) {
                    wr.println("Found Solution");
                    wr.println();
                    return cur; 
                }

                if (visited[cur.getX()][cur.getY()][cur.getCurDigit()]) continue;
                visited[cur.getX()][cur.getY()][cur.getCurDigit()] = true;

                for (int i = 0; i < 4; i++){
                    char nextDir = dir[i];
                    State nextState = slide(cur,dx[i], dy[i],nextDir,algorithm,wr);
                    if (nextState != null && !visited[nextState.getX()][nextState.getY()][nextState.getCurDigit()]){
                        queue.add(nextState);
                    }
                }
                wr.println();
            }
        } catch (IOException e){
            System.out.println("Gagal menulis iterasi ke file iterasi.txt");
        }
        return null;
    }

    private State solveBFS(){
        Queue<State> queue = new LinkedList<>();
        boolean[][][] visited = new boolean[map.getRowCount()][map.getColCount()][11];

        try (PrintWriter wr =new PrintWriter(new FileWriter(iterationFilePath))){
            State start = new State(map.getInitialX(), map.getInitialY(), 0, 0, "", 0, false);
            queue.add(start);

            while(!queue.isEmpty()){
                State cur = queue.poll();
                totalIteration++;
                wr.println("=== Iteration " + totalIteration + " ===");
                wr.println("Initial");
                for (char[] row : loadSteps(0, cur.getSteps())){
                    wr.println(new String(row));
                }
                wr.println();
                for (int i = 1; i <= cur.getSteps().length(); i++){
                    wr.println("Step " + i + " : " + cur.getSteps().charAt(i-1));
                    for (char[] row : loadSteps(i, cur.getSteps())){
                        wr.println(new String(row));
                    }
                    wr.println();
                }

                if (map.getBoard()[cur.getX()][cur.getY()] == 'O' && cur.getCurDigit() > map.getMaxDigit()) {
                    wr.println("Found Solution");
                    wr.println();
                    return cur; 
                }

                if (visited[cur.getX()][cur.getY()][cur.getCurDigit()]) continue;
                visited[cur.getX()][cur.getY()][cur.getCurDigit()] = true;

                for (int i = 0; i < 4; i++){
                    char nextDir = dir[i];
                    State nextState = slide(cur,dx[i], dy[i],nextDir,"BFS",wr);
                    if (nextState != null && !visited[nextState.getX()][nextState.getY()][nextState.getCurDigit()]){
                        queue.add(nextState);
                    }
                }
                wr.println();
            }
        } catch (IOException e){
            System.out.println("Gagal menulis iterasi ke file iterasi.txt");
        }
        return null;
    }

    private State solveDFS(){
        Deque<State> stack = new ArrayDeque<>();
        boolean[][][] visited = new boolean[map.getRowCount()][map.getColCount()][11];

        try (PrintWriter wr =new PrintWriter(new FileWriter(iterationFilePath))){
            State start = new State(map.getInitialX(), map.getInitialY(), 0, 0, "", 0, false);
            stack.push(start);

            while(!stack.isEmpty()){
                State cur = stack.pop();
                totalIteration++;
                wr.println("=== Iteration " + totalIteration + " ===");
                wr.println("Initial");
                for (char[] row : loadSteps(0, cur.getSteps())){
                    wr.println(new String(row));
                }
                wr.println();
                for (int i = 1; i <= cur.getSteps().length(); i++){
                    wr.println("Step " + i + " : " + cur.getSteps().charAt(i-1));
                    for (char[] row : loadSteps(i, cur.getSteps())){
                        wr.println(new String(row));
                    }
                    wr.println();
                }

                if (map.getBoard()[cur.getX()][cur.getY()] == 'O' && cur.getCurDigit() > map.getMaxDigit()) {
                    wr.println("Found Solution");
                    wr.println();
                    return cur; 
                }

                if (visited[cur.getX()][cur.getY()][cur.getCurDigit()]) continue;
                visited[cur.getX()][cur.getY()][cur.getCurDigit()] = true;

                for (int i = 0; i < 4; i++){
                    char nextDir = dir[i];
                    State nextState = slide(cur,dx[i], dy[i],nextDir,"DFS",wr);
                    if (nextState != null && !visited[nextState.getX()][nextState.getY()][nextState.getCurDigit()]){
                        stack.push(nextState);
                    }
                }
                wr.println();
            }
        } catch (IOException e){
            System.out.println("Gagal menulis iterasi ke file iterasi.txt");
        }
        return null;
    }

    public char[][] loadSteps(int n, String steps){
        char[][] board = new char[map.getRowCount()][map.getColCount()];
        for (int i = 0; i < map.getRowCount(); i++){
            for (int j = 0; j < map.getColCount(); j++){
                board[i][j] = map.getBoard()[i][j];
            }
        }
        int curX = map.getInitialX();
        int curY = map.getInitialY();
        board[curX][curY] = 'Z';

        for(int i = 0; i < n; i++){
            char move = steps.charAt(i);
            int dX = 0;
            int dY = 0;
            if(move == 'U') dX =-1;
            else if(move == 'D') dX = 1;
            else if(move == 'L') dY = -1;
            else if(move == 'R') dY = 1;

            while (true){
                int newX = curX + dX;
                int newY = curY + dY;
                if (board[newX][newY] == 'X')break;

                char oriTile = map.getBoard()[curX][curY];
                if (Character.isDigit(oriTile)) {
                    board[curX][curY] = oriTile;
                } else if (oriTile != 'O' && oriTile != 'X' && oriTile != 'L') {
                    board[curX][curY] = '*';
                } else {
                    board[curX][curY] = oriTile;
                }

                curX = newX;
                curY = newY;
            }

            board[curX][curY] = 'Z';
        }
        return board;
    }

    public void printBoard(char[][] board){
        for (char[] row : board){
            System.out.println(new String(row));
        }
        System.out.println();
    }

    public int getTotalIteration(){return totalIteration;}
}