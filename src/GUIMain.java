import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class GUIMain extends JFrame {
    private Map map;
    private Algorithm algorithm;
    private State solution;
    private String currentAlgorithm = "UCS";
    private String currentHeuristic = "H1";
    private int currentStep = 0;
    private long executionTime;
    private Timer animationTimer;
    private int animationFromStep = 0;
    private int animationToStep = 0;
    private float animationProgress = 1f;

    private static final int ANIMATION_DURATION_MS = 180;
    private static final int ANIMATION_FRAME_MS = 16;
    private JPanel boardPanel;
    private JComboBox<String> algorithmSelector;
    private JComboBox<String> heuristicSelector;
    private JButton loadButton, solveButton, saveButton;
    private JLabel fileLabel, costLabel, iterLabel, timeLabel, stepLabel;
    private JButton prevButton, nextButton;
    private JLabel solutionLabel;

    private static final int CELL_SIZE = 40;
    private static final int MIN_CELL_SIZE = 14;
    
    // Minimal Modern Color Palette
    private static final Color BG_COLOR = new Color(250, 250, 250);      // Off-white
    private static final Color PANEL_BG = new Color(245, 245, 245);      // Light gray
    private static final Color ACCENT_COLOR = new Color(0, 120, 150);    // Teal accent
    private static final Color TEXT_PRIMARY = new Color(30, 30, 30);     // Dark text
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100); // Gray text
    private static final Color BORDER_COLOR = new Color(220, 220, 220);  // Subtle border

    public GUIMain(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("TUCIL 3: PATHFINDING UCS, GBFS, A* ALGORITHM");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BG_COLOR);

        boardPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };

        boardPanel.setPreferredSize(new Dimension(500, 600));
        boardPanel.setBackground(Color.WHITE);
        boardPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        boardPanel.setFocusable(true);

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boardPanel.requestFocusInWindow();
            }
        });
        boardPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                handleBoardKeyPress(e);
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(450, 600));
        controlPanel.setBackground(BG_COLOR);

        JPanel filePanel = createFilePanel();
        controlPanel.add(filePanel);
        controlPanel.add(Box.createVerticalStrut(15));

        JPanel algoPanel = createAlgoritmPanel();
        controlPanel.add(algoPanel);
        controlPanel.add(Box.createVerticalStrut(15));

        JPanel solvePanel = createSolvePanel();
        controlPanel.add(solvePanel);
        controlPanel.add(Box.createVerticalStrut(15));
        
        JPanel infoPanel = createInfoPanel();
        controlPanel.add(infoPanel);
        controlPanel.add(Box.createVerticalStrut(15));
        
        JPanel playbackPanel = createPlaybackPanel();
        controlPanel.add(playbackPanel);
        controlPanel.add(Box.createVerticalStrut(15));

        JPanel savePanel = new JPanel();
        savePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        savePanel.setBackground(BG_COLOR);
        
        saveButton = new JButton("SAVE");
        saveButton.setEnabled(false);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        saveButton.setBackground(ACCENT_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        saveButton.addActionListener(e -> saveResult());
        savePanel.add(saveButton);
        controlPanel.add(savePanel);

        controlPanel.add(Box.createVerticalGlue());

        mainPanel.add(new JScrollPane(boardPanel), BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createFilePanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        fileLabel = new JLabel("No file loaded.");
        fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileLabel.setForeground(TEXT_SECONDARY);

        loadButton = new JButton("LOAD FILE");
        loadButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        loadButton.setBackground(ACCENT_COLOR);
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);
        loadButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        loadButton.addActionListener(e -> loadFile());

        panel.add(fileLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loadButton);

        return panel;
    }
    
    private JPanel createAlgoritmPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel algoLabel = new JLabel("Select Algorithm:");
        algoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        algoLabel.setForeground(TEXT_PRIMARY);
        
        algorithmSelector = new JComboBox<>(new String[]{"UCS", "GBFS", "A*", "BFS", "DFS"});
        algorithmSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        algorithmSelector.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        algorithmSelector.addActionListener(e -> {
            currentAlgorithm = (String) algorithmSelector.getSelectedItem();
            boolean useHeuristic = !currentAlgorithm.equals("UCS") &&
                                    !currentAlgorithm.equals("BFS") &&
                                    !currentAlgorithm.equals("DFS");
            heuristicSelector.setEnabled(useHeuristic);
        });

        JLabel heuristicLabel = new JLabel("Select Heuristic:");
        heuristicLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        heuristicLabel.setForeground(TEXT_PRIMARY);
        
        heuristicSelector = new JComboBox<>(new String[]{"H1", "H2", "H3"});
        heuristicSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        heuristicSelector.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        heuristicSelector.addActionListener(e -> currentHeuristic = (String) heuristicSelector.getSelectedItem());

        panel.add(algoLabel);
        panel.add(algorithmSelector);
        panel.add(Box.createVerticalStrut(10));
        panel.add(heuristicLabel);
        panel.add(heuristicSelector);

        return panel;
    }

    private JPanel createSolvePanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        solveButton = new JButton("SOLVE");
        solveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        solveButton.setEnabled(false);
        solveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        solveButton.setBackground(ACCENT_COLOR);
        solveButton.setForeground(Color.WHITE);
        solveButton.setFocusPainted(false);
        solveButton.setBorderPainted(false);
        solveButton.addActionListener(e -> solve());

        panel.add(solveButton);
        return panel;
    }

    private JPanel createInfoPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Result Info");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(TEXT_PRIMARY);

        solutionLabel = new JLabel("Solution: -");
        costLabel = new JLabel("Cost: -");
        iterLabel = new JLabel("Iterations: -");
        timeLabel = new JLabel("Execution Time: - ms");
        stepLabel = new JLabel("Step: 0/?");

        Font infoFont = new Font("Segoe UI", Font.PLAIN, 11);
        Color infoColor = TEXT_SECONDARY;
        
        solutionLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        solutionLabel.setForeground(infoColor);
        costLabel.setFont(infoFont);
        costLabel.setForeground(infoColor);
        iterLabel.setFont(infoFont);
        iterLabel.setForeground(infoColor);
        timeLabel.setFont(infoFont);
        timeLabel.setForeground(infoColor);
        stepLabel.setFont(infoFont);
        stepLabel.setForeground(infoColor);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(solutionLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(costLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(iterLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(timeLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(stepLabel);

        return panel;
    }

    private JPanel createPlaybackPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel helpLabel = new JLabel("<html>Controls:<br>[← →] Prev/Next<br>[ESC] Jump to step</html>");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        helpLabel.setForeground(TEXT_SECONDARY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(PANEL_BG);
        
        prevButton = new JButton("< Prev");
        prevButton.setEnabled(false);
        prevButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        prevButton.setBackground(ACCENT_COLOR);
        prevButton.setForeground(Color.WHITE);
        prevButton.setFocusPainted(false);
        prevButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        prevButton.addActionListener(e -> previousStep());

        nextButton = new JButton("Next >");
        nextButton.setEnabled(false);
        nextButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nextButton.setBackground(ACCENT_COLOR);
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        nextButton.addActionListener(e -> nextStep());

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        panel.add(helpLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        return panel;
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser("../test/input");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            map = new Map();
            if (map.parseFile(file.getAbsolutePath())) {
                fileLabel.setText("Loaded: " + file.getName());
                solveButton.setEnabled(true);
                resetResult();
                boardPanel.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void solve() {
        if (map == null) return;

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        solveButton.setEnabled(false);
        algorithm = new Algorithm(map);
        algorithm.setHeuristicType(currentHeuristic);

        File iterationDir = new File("../test/iteration");
        iterationDir.mkdirs();
        String iterationPath = new File(iterationDir, "temp.txt").getAbsolutePath();
        algorithm.setIterationFilePath(iterationPath);

        long startTime = System.currentTimeMillis();
        solution = algorithm.solve(currentAlgorithm);
        executionTime = System.currentTimeMillis() - startTime;

        if (solution != null) {
            currentStep = 0;
            costLabel.setText("Cost: " + solution.getG());
            iterLabel.setText("Iterations: " + algorithm.getTotalIteration());
            timeLabel.setText("Time: " + executionTime + " ms");
            solutionLabel.setText("Solution: " + solution.getSteps());
            updateStepLabel();

            prevButton.setEnabled(false);
            nextButton.setEnabled(true);
            saveButton.setEnabled(true);

            updateBoard();
            boardPanel.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "No solution found", "Result", JOptionPane.INFORMATION_MESSAGE);
        }

        solveButton.setEnabled(true);
    }

    private void nextStep() {
        if (solution != null && currentStep < solution.getSteps().length()) {
            animateToStep(currentStep + 1);
        }
    }

    private void previousStep() {
        if (currentStep > 0) {
            animateToStep(currentStep - 1);
        }
    }

    private void handleBoardKeyPress(KeyEvent e) {
        if (solution == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                previousStep();
                break;
            case KeyEvent.VK_RIGHT:
                nextStep();
                break;
            case KeyEvent.VK_ESCAPE:
                jumpToStep();
                break;
        }
    }

    private void jumpToStep() {
        if (solution == null) return;

        String input = JOptionPane.showInputDialog(this, 
            "Jump to step (0-" + solution.getSteps().length() + "):", 
            "0");
        
        if (input != null) {
            try {
                int step = Integer.parseInt(input.trim());
                if (step >= 0 && step <= solution.getSteps().length()) {
                    currentStep = step;
                    updateBoard();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Step harus antara 0 dan " + solution.getSteps().length(), 
                        "Invalid Input", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Masukkan angka yang valid", 
                    "Invalid Input", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void updateBoard() {
        prevButton.setEnabled(currentStep > 0);
        nextButton.setEnabled(currentStep < (solution != null ? solution.getSteps().length() : 0));
        updateStepLabel();
        boardPanel.repaint();
    }

    private void updateStepLabel() {
        if (solution != null) {
            stepLabel.setText("Step: " + currentStep + "/" + solution.getSteps().length());
        }
    }

    private void drawBoard(Graphics g) {
        if (map == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        char[][] board = (solution != null) ? algorithm.loadSteps(currentStep, solution.getSteps()) : map.getBoard();

        char[][] fromBoard = null;
        char[][] toBoard = null;
        Point fromPos = null;
        Point toPos = null;

        boolean animating = animationTimer != null && animationTimer.isRunning();
        if (animating && solution != null) {
            fromBoard = algorithm.loadSteps(animationFromStep, solution.getSteps());
            toBoard = algorithm.loadSteps(animationToStep, solution.getSteps());
            fromPos = findPlayerPosition(fromBoard);
            toPos = findPlayerPosition(toBoard);
        }

        int[][] costs = map.getBoardCosts();

        int rows = map.getRowCount();
        int cols = map.getColCount();
        int cellSize = getCellSize(rows, cols);
        int boardWidth = cols * cellSize;
        int boardHeight = rows * cellSize;
        int offsetX = Math.max(0, (boardPanel.getWidth() - boardWidth) / 2);
        int offsetY = Math.max(0, (boardPanel.getHeight() - boardHeight) / 2);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = offsetX + (j * cellSize);
                int y = offsetY + (i * cellSize);
                char cell = board[i][j];
                int cost = costs[i][j];

                Color bgColor = getCostColor(cost);
                g2d.setColor(bgColor);
                g2d.fillRect(x, y, cellSize, cellSize);

                g2d.setColor(BORDER_COLOR);
                g2d.drawRect(x, y, cellSize, cellSize);

                if (cell == 'Z') {
                    if (!animating) {
                        g2d.setColor(ACCENT_COLOR);
                        int pad = Math.max(2, cellSize / 6);
                        g2d.fillOval(x + pad, y + pad, cellSize - (pad * 2), cellSize - (pad * 2));
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Segoe UI", Font.BOLD, Math.max(10, cellSize / 2)));
                        FontMetrics fm = g2d.getFontMetrics();
                        String symbol = "Z";
                        int sx = x + (cellSize - fm.stringWidth(symbol)) / 2;
                        int sy = y + ((cellSize - fm.getHeight()) / 2) + fm.getAscent();
                        g2d.drawString(symbol, sx, sy);
                    }
                } else {
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, Math.max(10, cellSize / 2)));
                    g2d.setColor(getSymbolColor(cell));
                    String symbol = String.valueOf(cell);
                    FontMetrics fm = g2d.getFontMetrics();
                    int sx = x + (cellSize - fm.stringWidth(symbol)) / 2;
                    int sy = y + ((cellSize - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(symbol, sx, sy);
                }
            }
        }

        if (animating && fromPos != null && toPos != null) {
            float px = lerp(fromPos.x, toPos.x, animationProgress);
            float py = lerp(fromPos.y, toPos.y, animationProgress);

            int zx = offsetX + Math.round(px * cellSize);
            int zy = offsetY + Math.round(py * cellSize);

            g2d.setColor(new Color(33, 150, 243));
            int pad = Math.max(2, cellSize / 6);
            g2d.fillOval(zx + pad, zy + pad, cellSize - (pad * 2), cellSize - (pad * 2));

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, Math.max(10, cellSize / 2)));
            FontMetrics fm = g2d.getFontMetrics();
            String symbol = "Z";
            int sx = zx + (cellSize - fm.stringWidth(symbol)) / 2;
            int sy = zy + ((cellSize - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(symbol, sx, sy);
        }
    }

    private int getCellSize(int rows, int cols) {
        if (rows <= 0 || cols <= 0) return CELL_SIZE;
        int panelW = Math.max(1, boardPanel.getWidth());
        int panelH = Math.max(1, boardPanel.getHeight());
        int fitSize = Math.min(panelW / cols, panelH / rows);
        int size = Math.min(CELL_SIZE, fitSize);
        return Math.max(MIN_CELL_SIZE, size);
    }

    private Color getCostColor(int cost) {
        if (cost == 0) return new Color(240, 240, 240);  // Very light gray
        // Softer gradient
        int normalized = Math.min(200, cost * 4);
        return new Color(255 - normalized/2, 200 - normalized/3, 100 + normalized/4);
    }

    private Color getSymbolColor(char symbol) {
        switch (symbol) {
            case 'Z': return Color.BLUE;
            case 'O': return Color.RED;
            case 'X': return Color.BLACK;
            case 'L': return new Color(255, 165, 0); // Orange
            case '*': return new Color(128, 128, 128); // Gray
            default:
                if (Character.isDigit(symbol)) return Color.GREEN;
                return Color.BLACK;
        }
    }

    private void saveResult() {
        if (solution == null) return;

        JFileChooser chooser = new JFileChooser("../test/output");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (java.io.PrintWriter wr = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                wr.println("Algoritma: " + currentAlgorithm);
                if (!currentAlgorithm.equals("UCS") && !currentAlgorithm.equals("BFS") && !currentAlgorithm.equals("DFS")) {
                    wr.println("Heuristic: " + currentHeuristic);
                }
                wr.println("Solusi : " + solution.getSteps());
                wr.println("Cost : " + solution.getG());
                wr.println("Iterasi: " + algorithm.getTotalIteration());
                wr.println("Waktu: " + executionTime + " ms");
                wr.println();
                wr.println("Initial");
                for (char[] row : algorithm.loadSteps(0, solution.getSteps())) {
                    wr.println(new String(row));
                }
                wr.println();
                for (int i = 1; i <= solution.getSteps().length(); i++) {
                    wr.println("Step " + i + " : " + solution.getSteps().charAt(i - 1));
                    for (char[] row : algorithm.loadSteps(i, solution.getSteps())) {
                        wr.println(new String(row));
                    }
                    wr.println();
                }
                JOptionPane.showMessageDialog(this, "Saved to: " + file.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetResult() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        solution = null;
        currentStep = 0;
        solutionLabel.setText("Solution: -");
        costLabel.setText("Cost: -");
        iterLabel.setText("Iterations: -");
        timeLabel.setText("Time: -");
        stepLabel.setText("Step: 0/?");
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        saveButton.setEnabled(false);
        boardPanel.repaint();
    }

    private void animateToStep(int targetStep) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationFromStep = currentStep;
        animationToStep = targetStep;
        animationProgress = 0f;

        animationTimer = new Timer(ANIMATION_FRAME_MS, e -> {
            animationProgress += (float) ANIMATION_FRAME_MS / ANIMATION_DURATION_MS;

            if (animationProgress >= 1f) {
                animationProgress = 1f;
                ((Timer) e.getSource()).stop();
                currentStep = animationToStep;
                updateBoard();
            } else {
                boardPanel.repaint();
            }
        });

        animationTimer.start();
    }
    private Point findPlayerPosition(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 'Z') {
                    return new Point(j, i);
                }
            }
        }
        return null;
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIMain());
    }
}
