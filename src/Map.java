import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Map {
    private int N, M; 
    private char[][] board; 
    private int[][] boardCosts; 
    private int maxDigit; 
    private int initX, initY;
    private int targetX, targetY;

    public boolean parseFile(String filepath){
        try {
            Scanner sc = new Scanner(new File(filepath));
            if (!sc.hasNextInt()){
                System.out.println("[ERROR] File kosong atau tidak memiliki penentu dimensi N M!");
            }
            N = sc.nextInt();
            M = sc.nextInt();
            board = new char[N][M];
            boardCosts = new int[N][M];
            int countZ = 0, countO = 0;
            boolean[] digitInBoard = new boolean[10];

            for (int i = 0; i < N; i++){
                if(!sc.hasNext()){
                    System.out.println("[ERROR] Jumlah baris papan kurang dari " + N + "! Ngitung dong atleast :(");
                    return false; 
                }
                String line = sc.next();
                if (line.length() != M){
                    System.out.println("[ERROR] Panjang baris ke-" + (i+1) + " tidak sama dengan M! Ngitung dong atleast :(");
                    return false;
                }
                board[i] = line.toCharArray();

                for (int j = 0; j<M; j++){
                    char c = board[i][j];
                    if (c == 'Z') {
                        countZ++;
                        initX = i;
                        initY = j;
                    }
                    else if (c == 'O') {
                        countO++;
                        targetX = i;
                        targetY = j;
                    }
                    else if (Character.isDigit(c)){
                        int digit = Character.getNumericValue(c);
                        digitInBoard[digit] = true;
                        if (digit > maxDigit){
                            maxDigit = digit;
                        }
                    } else if (c != 'X' && c != '*' && c != 'L'){
                        System.out.println("[ERROR] Karakter tidak dikenal '" + c + "' di papan! Ngetik yang bener dong :(");
                        return false;
                    }

                }
            }
            if (countZ != 1){
                System.out.println("[ERROR] Papan harus memiliki tepat satu Z. Terdapat " + countZ + " di papan! Fokus ke satu player dong :(");
                return false; 
            }
            if (countO != 1){
                System.out.println("[ERROR] Papan harus memiliki tepat satu O. Terdapat " + countO + " di papan! Sama seperti di kehidupan kita hanya boleh punya satu pacar yah...");
                return false;
            }
            boolean foundNumberAfterGap = false; 
            for (int i = 0; i < 10; i++){
                if (digitInBoard[i]){
                    if(foundNumberAfterGap){
                        System.out.println("[ERROR] Urutan tidak valid! Tolong ngurut dong angka di papannya...");
                        return false; 
                    }
                } else {
                    foundNumberAfterGap = true;
                }
            }
            for (int i = 0; i < N; i++){
                for (int j = 0; j < M ; j++){
                    if (!sc.hasNextInt()){
                        System.out.println("[ERROR] Data cost kurang untuk papan berukuran N x M :(");
                        return false;
                    }
                    boardCosts[i][j] = sc.nextInt();
                }
            }
            sc.close();
            return true;
        } catch (FileNotFoundException e){
            System.out.println("[ERROR] File " + filepath + " tidak ditemukan!");
            return false; 
        }
    }

    public int getRowCount(){ return N;}
    public int getColCount(){ return M;}
    public char[][] getBoard(){ return board;}
    public int[][] getBoardCosts(){ return boardCosts;}
    public int getMaxDigit(){ return maxDigit;}
    public int getInitialX(){ return initX;}
    public int getInitialY(){ return initY;}
    public int getTargetX(){ return targetX;}
    public int getTargetY(){ return targetY;}
}