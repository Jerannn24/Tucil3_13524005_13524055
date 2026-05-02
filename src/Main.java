import java.io.*;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        Map map = new Map();
        System.out.println(">> Masukkan file input: ");
        String file = sc.nextLine().trim();
        String inputName = new File(file).getName();
        String inputPath = "../test/input/" + inputName;
        if (!map.parseFile(inputPath)) return;

        System.out.println(">> Algoritma apa yang anda pilih? (UCS/GBFS/A*)");
        String algorithm = sc.nextLine().toUpperCase();   

        String hrstk= "H1";
        if (algorithm.equals("GBFS") || algorithm.equals("A*")) {
            System.out.println(">> Heuristic apa yang anda pilih? (H1/H2/H3)" );
            hrstk = sc.nextLine().toUpperCase();
        }
        Algorithm solve = new Algorithm(map);
        solve.setHeuristicType(hrstk);
        File iterationDir = new File("../test/iteration");
        iterationDir.mkdirs();
        String iterationPath = new File(iterationDir, inputName).getPath();
        solve.setIterationFilePath(iterationPath);
        long start = System.currentTimeMillis();
        State goal = solve.solve(algorithm);
        long end = System.currentTimeMillis();

        System.out.println();
        if (goal!=null){
            String steps = goal.getSteps();
            System.out.println("Solusi yang ditemukan : " + steps);
            System.out.println("Cost dari Solusi : " + goal.getG());
            System.out.println();

            System.out.println("Initial");
            solve.printBoard(solve.loadSteps(0, steps));
            for (int i = 1; i <= steps.length(); i++){
                System.out.println("Step " + i + " : " + steps.charAt(i-1));
                solve.printBoard(solve.loadSteps(i,steps));
            }
            System.out.println(">> Waktu eksekusi: " + (end-start) + " ms");
            System.out.println(">> Banyak iterasi yang dilakukan: "+ solve.getTotalIteration() + " iterasi");

            System.out.println(">> Apakah Anda ingin melakukan playback? (Ya/Tidak) :");
            String playbackChoice = sc.nextLine().toUpperCase();
            if (playbackChoice.equals("YA")){
                playback(sc, solve, steps);
            }

            System.out.println(">> Apakah Anda ingin menyimpan solusi? (Ya/Tidak) : ");
            if (sc.nextLine().equalsIgnoreCase("Ya")){
                File outputDir = new File("../test/output");
                outputDir.mkdirs();
                String outputPath = new File(outputDir, inputName).getPath();
                save(outputPath, goal, solve);
            }
        } else {
            System.out.println("Solusi tidak ditemukan!");
            System.out.println("Anda bisa melihat iterasi yang dilakukan di file test/iteration/" + inputName);
        }
        sc.close();
    }

    private static void playback(Scanner sc, Algorithm solve, String steps){
        int curStep = 0;
        int maxStep = steps.length();
        boolean flowing = true; // Ini buat nandain kalau masih ngelakuin playback

        System.out.println("=== MODE PLAYBACK ===");
        System.out.println("[<] Mundur | [>] Maju | [ESC] Lompat | [Q] Keluar (Jangan lupa gunakan Enter)");

        while(flowing){
            System.out.println("Step " + curStep + " :");
            solve.printBoard(solve.loadSteps(curStep, steps));
            System.out.print("Input (< / > / ESC / Q) : ");
            String input = sc.nextLine().toUpperCase();
            switch(input){
                case ">" : 
                    if (curStep < maxStep) curStep++;
                    break;
                case "<":
                    if (curStep > 0) curStep--;
                    break;
                case "ESC":
                    System.out.println("Pada step berapa anda ingin melakukan playback :");
                    if (sc.hasNextInt()){
                        int inputStep = sc.nextInt();
                        if (inputStep >=0 && inputStep <= maxStep){
                            curStep = inputStep;
                        }
                    }
                    sc.nextLine();
                    break;
                case "Q":
                    flowing = false;
                    break;
            }
        }
    }

    private static void save(String path, State goal, Algorithm solve){
        try (PrintWriter wr = new PrintWriter(new FileWriter(path))){
            wr.println("Solusi : " + goal.getSteps());
            wr.println("Cost : " + goal.getG());
            wr.println("Initial");
            for (char[] row : solve.loadSteps(0,goal.getSteps())){
                wr.println(new String(row));
            }
            wr.println();
            for (int i = 1; i <= goal.getSteps().length();i++){
                wr.println("Step " + i + " : " + goal.getSteps().charAt(i-1));
                for (char[] row : solve.loadSteps(i,goal.getSteps())){
                    wr.println(new String(row));
                }
                wr.println();
            }
            System.out.println(">> Disimpan pada " + path);
        } catch(IOException e){
            System.out.println("Gagal menyimpan file.");
        }
    }
}