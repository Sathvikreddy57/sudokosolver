import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuGUI extends JFrame {
    private static final int SIZE = 9; // Size of the Sudoku grid (9x9)
    private JTextField[][] cells = new JTextField[SIZE][SIZE]; // Array to hold text fields for each cell

    public SudokuGUI() {
        setTitle("Sudoku Solver"); // Set the title of the window
        setSize(600, 600); // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the application exits when the window is closed
        setLayout(new GridLayout(SIZE + 1, SIZE)); // Set the layout to GridLayout to organize cells and button

        // Create grid of text fields for the Sudoku cells
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new JTextField(2); // Create a text field with width for 2 characters
                cells[i][j].setHorizontalAlignment(JTextField.CENTER); // Center align the text
                cells[i][j].setFont(new Font("Arial", Font.BOLD, 20)); // Set font style and size
                add(cells[i][j]); // Add the text field to the grid
            }
        }

        // Create Solve button
        JButton solveButton = new JButton("Solve"); // Create a button labeled "Solve"
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[][] board = new int[SIZE][SIZE]; // Initialize the board to hold Sudoku numbers
                if (getBoard(board)) { // Read the input from text fields
                    if (isValidBoard(board)) { // Check if the board configuration is valid
                        if (solveSudoku(board)) { // Solve the Sudoku puzzle
                            updateBoard(board); // Update the text fields with the solution
                        } else {
                            JOptionPane.showMessageDialog(null, "No solution exists"); // Notify if no solution is found
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Sudoku cannot be solved with this input"); // Notify if input is invalid
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input"); // Notify if input is invalid
                }
            }
        });

        // Add Solve button to the frame
        JPanel panel = new JPanel(); // Create a panel to hold the button
        panel.add(solveButton); // Add the button to the panel
        add(panel); // Add the panel to the frame
    }

    private boolean getBoard(int[][] board) {
        try {
            // Read the numbers from text fields and populate the board
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    String text = cells[i][j].getText().trim(); // Get and trim text from the cell
                    if (text.isEmpty()) {
                        board[i][j] = 0; // Empty cell is represented by 0
                    } else {
                        int num = Integer.parseInt(text); // Parse the number from the text
                        if (num < 1 || num > 9) { // Check if the number is valid (1-9)
                            return false;
                        }
                        board[i][j] = num; // Populate the board with the number
                    }
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false; // Catch any number format exceptions and return false
        }
    }

    private boolean isValidBoard(int[][] board) {
        // Check rows and columns for duplicates
        for (int i = 0; i < SIZE; i++) {
            boolean[] rowSeen = new boolean[SIZE + 1];
            boolean[] colSeen = new boolean[SIZE + 1];
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != 0) {
                    if (rowSeen[board[i][j]]) return false; // Duplicate in row
                    rowSeen[board[i][j]] = true;
                }
                if (board[j][i] != 0) {
                    if (colSeen[board[j][i]]) return false; // Duplicate in column
                    colSeen[board[j][i]] = true;
                }
            }
        }

        // Check 3x3 grids for duplicates
        for (int r = 0; r < SIZE; r += 3) {
            for (int c = 0; c < SIZE; c += 3) {
                boolean[] gridSeen = new boolean[SIZE + 1];
                for (int i = r; i < r + 3; i++) {
                    for (int j = c; j < c + 3; j++) {
                        if (board[i][j] != 0) {
                            if (gridSeen[board[i][j]]) return false; // Duplicate in 3x3 grid
                            gridSeen[board[i][j]] = true;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void updateBoard(int[][] board) {
        // Update the text fields with the solved Sudoku board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].setText(board[i][j] == 0 ? "" : String.valueOf(board[i][j])); // Set text to empty if value is 0
            }
        }
    }

    private boolean solveSudoku(int[][] board) {
        int[] emptyCell = findEmptyCell(board); // Find an empty cell
        if (emptyCell == null) {
            return true; // No empty cell found means puzzle is solved
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        // Try all possible numbers for the empty cell
        for (int num = 1; num <= SIZE; num++) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num; // Place the number in the cell
                if (solveSudoku(board)) { // Recursively solve the rest of the puzzle
                    return true;
                }
                board[row][col] = 0; // Undo move if it leads to no solution
            }
        }

        return false; // Trigger backtracking if no number fits
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        // Check if the number is valid in the row
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }

        // Check if the number is valid in the column
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }

        // Check if the number is valid in the 3x3 grid
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[] findEmptyCell(int[][] board) {
        // Find the first empty cell in the board
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    return new int[] {row, col};
                }
            }
        }
        return null; // Return null if no empty cell is found
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuGUI frame = new SudokuGUI(); // Create an instance of the SudokuGUI
            frame.setVisible(true); // Make the frame visible
        });
    }
}
