import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class MineSweeperforJgrasp {
    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int titleSize = 70;
    int numRows = 11;
    int numCols = 12;
    int boardWidth = numCols * titleSize;
    int boardHeight = numRows * titleSize;

    JFrame frame = new JFrame("MineSweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int mineCount = 30;
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0; // goal is to click all tiles except the ones containing mines
    boolean gameOver = false;

    JFrame homeFrame = new JFrame("Welcome to Minesweeper");

    MineSweeper() {
        // Set the look and feel for better UI experience
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the home page frame
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setSize(400, 300);
        homeFrame.setLocationRelativeTo(null);

        // Set a background image for the home page
        try {
            final BufferedImage backgroundImage = ImageIO.read(getClass().getResource("background.JPG"));
            homeFrame.setContentPane(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(backgroundImage, 0, 0, homeFrame.getWidth(), homeFrame.getHeight(), this);
                }
            });
        } catch (Exception e) { e.printStackTrace();}
        

        // Add the "Start" button for level selection
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Set the font size
        startButton.setPreferredSize(new Dimension(120, 40)); // Set the preferred size
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDifficultySelection(); // Show the Minesweeper game frame
            }
        });

        // Add components to the home frame
        homeFrame.setLayout(new GridBagLayout());

        // Create GridBagConstraints to center the button
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0); // Optional: Adjust insets as needed
        gbc.anchor = GridBagConstraints.CENTER;

        homeFrame.add(startButton, gbc);
        


        // Make the home frame visible
        homeFrame.setVisible(true);
    }

    
    private void showDifficultySelection() {
        // Show a dialog to select the difficulty level
        Object[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(homeFrame,
                "Choose Difficulty Level",
                "Difficulty",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        // Set difficulty level based on the user's choice
        switch (choice) {
            case 0: // Easy
                numRows = 5;
                numCols = 6;
                mineCount = 5;
                break;
            case 1: // Medium
                numRows = 8;
                numCols = 10;
                mineCount = 10;
                break;
            case 2: // Hard
                numRows = 11;
                numCols = 12;
                mineCount = 30;
                break;
            default:
                // Default to Easy if the user closes the dialog
                numRows = 5;
                numCols = 6;
                mineCount = 5;
                break;
        }

        homeFrame.dispose(); // Close the home page
        showGameFrame(); // Show the Minesweeper game frame
    }

    private void showGameFrame() {
        //disposeGameFrame();
       
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Number of Mines" + " " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        boardPanel.setBackground(Color.BLUE);
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();
                        // left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        // right click
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("F");
                            } else if (tile.getText() == "F") {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }
        frame.setVisible(true);
        setMines();
    }

    void setMines() {
        mineList = new ArrayList<MineTile>();
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("B");
        }
        gameOver = true;
        textLabel.setText("Game Over!");
        showTryAgainLink();
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }
        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;
        int minesFound = 0;

        // top
        minesFound += countMine(r - 1, c - 1); // top left
        minesFound += countMine(r - 1, c); // top
        minesFound += countMine(r - 1, c + 1); // top right

        // left and right
        minesFound += countMine(r, c - 1); // left
        minesFound += countMine(r, c + 1); // right

        // bottom 3
        minesFound += countMine(r + 1, c - 1); // bottom left
        minesFound += countMine(r + 1, c); // bottom
        minesFound += countMine(r + 1, c + 1); // bottom right

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");

            // top 3
            checkMine(r - 1, c - 1); // top left
            checkMine(r - 1, c); // top
            checkMine(r - 1, c + 1); // top right

            // left and right
            checkMine(r, c - 1); // left
            checkMine(r, c + 1); // right

            // bottom 3
            checkMine(r + 1, c - 1); // bottom left
            checkMine(r + 1, c); // bottom
            checkMine(r + 1, c + 1); // bottom right
        }
        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
            showWinnerPage();
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }

    private void showWinnerPage() {
        // Create a new JFrame for the winner page
        JFrame winnerFrame = new JFrame("Congratulations!");
        winnerFrame.setSize(600, 300);
        winnerFrame.setLocationRelativeTo(null);
        winnerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // Create a JLabel for the winner message
        JLabel winnerLabel = new JLabel("Congratulations! You've cleared all mines!");
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        winnerLabel.setHorizontalAlignment(JLabel.CENTER);
    
        // Create a JButton for restarting the game
        JButton restartButton = new JButton("Restart Game");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                winnerFrame.dispose(); // Close the winner frame
                restartGame(); // Restart the game
            }
        });
    
        // Create a JPanel to hold the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(restartButton);
    
        // Create a layout for the winner frame
        winnerFrame.setLayout(new BorderLayout());
        winnerFrame.add(winnerLabel, BorderLayout.CENTER);
        winnerFrame.add(buttonPanel, BorderLayout.SOUTH);
    
        // Make the winner frame visible
        winnerFrame.setVisible(true);
    }

    void showTryAgainLink() {
        // Use a final array to store the reference
        final JLabel[] tryAgainLabel = {new JLabel("<html><u>Try Again</u></html>")};
        tryAgainLabel[0].setForeground(Color.BLUE.darker());
        tryAgainLabel[0].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tryAgainLabel[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    restartGame();
                    tryAgainLabel[0].setVisible(false);
                }
            }
        });

        // Add the label to the frame
        JPanel tryAgainPanel = new JPanel();
        tryAgainPanel.add(tryAgainLabel[0]);
        frame.add(tryAgainPanel, BorderLayout.SOUTH);

        // Update the text label
        textLabel.setText("Game Over! Click 'Try Again' to play again.");

        // Repaint the frame to ensure changes are visible
        frame.revalidate();
        frame.repaint();
    }
    

    void restartGame() {
        // Reset game state
        tilesClicked = 0;
        gameOver = false;
        
        // Clear mine list
        mineList.clear();

        // Reset button states and labels
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = board[r][c];
                tile.setEnabled(true);
                tile.setText("");
            }
        }

        // Reset text label
        textLabel.setText("MineSweeper" + Integer.toString(mineCount));

        // Start a new game
        setMines();
    }

    public static void main(String[] args) throws Exception {
        MineSweeper mineSweeper = new MineSweeper();
    }
}
