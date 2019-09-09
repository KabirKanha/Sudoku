package com.kabirkanha.sudoku;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class runs the sudoku solver.
 */
class Solver {
    /**
     * This is the heuristic; it stores the remaining possible values for each cell.
     */
    private ArrayList<ArrayList<HashSet<Integer>>> cellSets = new ArrayList<>();

    /**
     * Stores the last printed values of the sets to avoid duplicate printing of the same stage in the console.
     */
    private ArrayList<ArrayList<HashSet<Integer>>> cellSetsChange = new ArrayList<>();

    /**
     * The main frame that contains the Sudoku grid.
     */
    private JFrame mainframe = new JFrame();

    /**
     * Sets the size of the whole grid.
     */
    static final int size = 9;

    /**
     * Sets the size of one sub-grid.
     */
    static final int gridSize = (int) (Math.sqrt(size));

    /**
     * Button to allow file input.
     */
    private JButton btn_input = new JButton("FILE INPUT");
    /**
     * Button to enable edits to the grid.
     */
    private JButton btn_edit = new JButton("EDIT");
    /**
     * Button to disable edits to the grid; also creates a checkpoint to reset to.
     */
    private JButton btn_lock = new JButton("LOCK");
    /**
     * Button to solve the sudoku.
     */
    private JButton btn_solve = new JButton("SOLVE");
    /**
     * Button to reset the sudoku to the last locked state.
     */
    private JButton btn_reset = new JButton("RESET");
    /**
     * Button to start over.
     */
    private JButton btn_new = new JButton("NEW GRID");
    /**
     * Button to verify a solve.
     */
    private JButton btn_verify = new JButton("VERIFY");

    /**
     * 2D integer array to store the grid data in numeric form.
     */
    private int[][] grid = new int[size][size];
    /**
     * 2D integer array to store number of backtracks from a particular cell.
     */
    private int[][] flagBack = new int[size][size];
    /**
     * Stores the total number of backtracks, to report after the solve.
     */
    private int backtrackCnt = 0;

    /**
     * 2D button array for the sudoku grid.
     */
    private JButton[][] buttons = new JButton[size][size];

    /**
     * 2D boolean array to store whether a confirmed cell has updated others or not.
     */
    private boolean[][] flag = new boolean[size][size];

    // Set up the look and feel to make the UI look good.
    static {
        try {
            //Using Nimbus Look and Feel
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            Logger.getLogger(Solver.class.getName()).log(Level.WARNING, "Unable to set Nimbus Look and Feel", e);
            //Can add some other look and feel here.
        }
    }

    /**
     * Constructor of this class; calls the solver.
     */
    Solver() {
        start();
    }

    /**
     * Starts the execution of the solver.
     */
    private void start() {
        //Initialise the sets.
        initialiseSet(cellSets);
        initialiseSet(cellSetsChange);

        //Add all possible values to the set.
        addAllValuesToSet();

        //Declare the JPanels.
        JPanel panelSudoku = new JPanel();
        JPanel panelOptions = new JPanel();
        JPanel panelMain = new JPanel();

        //Set the layout of the panels.
        panelSudoku.setLayout(new GridLayout(size, size));
        panelOptions.setPreferredSize(new Dimension(200, 0));
        panelOptions.setLayout(new GridLayout(7, 1, 0, 2));

        //Set borders of the buttons.
        int tb;
        int lb;
        int rb;
        int bb;
        for (int i = 0; i < size; i++) {
            bb = isLast(i);
            tb = isFirst(i);
            for (int j = 0; j < size; ++j) {
                buttons[i][j] = new JButton();
                rb = isLast(j);
                lb = isFirst(j);
                buttons[i][j].setBorder(BorderFactory.createMatteBorder(tb, lb, bb, rb, Color.DARK_GRAY));
                buttons[i][j].setText("");
                buttons[i][j].setPreferredSize(new Dimension(50, 50));
                buttons[i][j].setFont(new Font("DialogInput", Font.BOLD, 24));
                addOnClickListener(buttons[i][j], i, j);
                panelSudoku.add(buttons[i][j]);
            }
        }

        //Set the visual appearance of the buttons.
        setButtonFont(btn_input);
        setButtonFont(btn_edit);
        setButtonFont(btn_lock);
        setButtonFont(btn_reset);
        setButtonFont(btn_new);
        setButtonFont(btn_verify);

        //Set a different look to the solve button.
        btn_solve.setFont(new Font("DialogInput", Font.BOLD, 24));
        btn_solve.setBackground(Color.ORANGE);

        //Set the initial status of the buttons.
        btn_edit.setEnabled(false);
        btn_solve.setEnabled(false);
        btn_reset.setEnabled(false);
        btn_verify.setEnabled(false);

        //Add the buttons to the panel.
        panelOptions.add(btn_input);
        panelOptions.add(btn_edit);
        panelOptions.add(btn_lock);
        panelOptions.add(btn_solve);
        panelOptions.add(btn_reset);
        panelOptions.add(btn_new);
        panelOptions.add(btn_verify);

        //Set the appearance of the main panel.
        panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.LINE_AXIS));
        panelMain.add(Box.createRigidArea(new Dimension(5, 0)));
        panelMain.add(panelSudoku);
        panelMain.add(Box.createRigidArea(new Dimension(5, 0)));
        panelMain.add(panelOptions);
        panelMain.add(Box.createRigidArea(new Dimension(5, 0)));

        //Set the appearance of the frame.
        mainframe.add(panelMain);
        mainframe.setTitle("Sudoku Solver");
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.setResizable(false);
        mainframe.pack();
        mainframe.setVisible(true);

        //Adding Action Listeners

        btn_input.addActionListener(e -> {
            //Reset everything.
            addAllValuesToSet();

            JFileChooser chooser = new JFileChooser();

            //Disable multi-selection of files.
            chooser.setMultiSelectionEnabled(false);

            //Disable all files from being allowed.
            chooser.setAcceptAllFileFilterUsed(false);

            //Set dialog of the message box.
            chooser.setDialogTitle("Choose a CSV file for input");

            //Set filter.
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        String filename = f.getName().toLowerCase();
                        return filename.endsWith(".csv");
                    }
                }

                @Override
                public String getDescription() {
                    return "CSV Files (*.csv)";
                }
            });
            Scanner in = null;

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                try {
                    in = new Scanner(selectedFile);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                for (int i = 0; i < size; ++i) {
                    int k = 0;
                    String temp;
                    if (in != null && in.hasNextLine()) {
                        temp = in.nextLine();
                        StringBuilder buildup = new StringBuilder();
                        for (int j = 0; j < temp.length() && k < size; ++j) {
                            if (temp.charAt(j) == ',') {
                                buttons[i][k].setText(buildup.toString());
                                buildup = new StringBuilder();
                                k++;
                            } else {
                                buildup.append(temp.charAt(j));
                            }
                            if (j == temp.length() - 1) {
                                buttons[i][k].setText(buildup.toString());
                                ++k;
                                while (k < size) {
                                    buttons[i][k].setText("");
                                    ++k;
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < size; ++i) {
                    for (int j = 0; j < size; ++j) {
                        if (!(buttons[i][j].getText().equals("")))
                            grid[i][j] = Integer.parseInt(buttons[i][j].getText());
                        else
                            grid[i][j] = 0;
                    }
                }
            }
        });

        btn_edit.addActionListener(e -> {
            Choice.enableButtons(buttons);
            addAllValuesToSet();
            btn_input.setEnabled(true);
            btn_solve.setEnabled(false);
            btn_edit.setEnabled(false);
            btn_lock.setEnabled(true);
            btn_verify.setEnabled(false);
        });

        btn_lock.addActionListener(e -> {
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    buttons[i][j].setEnabled(false);
                    if (!(buttons[i][j].getText().equals(""))) {
                        grid[i][j] = Integer.parseInt(buttons[i][j].getText());
                        buttons[i][j].setForeground(Color.BLACK);
                    } else
                        grid[i][j] = 0;
                }
            }
            btn_input.setEnabled(false);
            btn_solve.setEnabled(true);
            btn_lock.setEnabled(false);
            btn_edit.setEnabled(true);
            btn_reset.setEnabled(false);
        });

        btn_solve.addActionListener(e -> {
            if (checkIfEnough()) {
                btn_reset.setEnabled(true);
                //Update cell sets from input.
                updateFromGrid();
                boolean flag = true;
                try {
                    solveWithBacktrack();
                } catch (StackOverflowError t) {
                    flag = false;
                    for (int i = 0; i < size; ++i) {
                        for (int j = 0; j < size; ++j) {
                            buttons[i][j].setText("");
                            addAllValuesToSet();
                            grid[i][j] = 0;
                        }
                    }
                    System.out.println("\n\nSOLVE ABORTED.");
                    JOptionPane.showMessageDialog(mainframe, "Too few values entered.\nStack will overflow.\nSOLVE ABORTED.", "Stack Overflow Error", JOptionPane.ERROR_MESSAGE);
                }
                if (flag) {
                    btn_verify.setEnabled(true);
                    printSets();
                    System.out.println(ConsoleColors.GREEN_BOLD + "\nSOLVE COMPLETED." + ConsoleColors.RESET);
                }
                System.out.println(ConsoleColors.RED + "Backtracks: " + backtrackCnt + ConsoleColors.RESET);
            } else {
                JOptionPane.showMessageDialog(mainframe, "Please enter at least 16 values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        btn_reset.addActionListener(e -> {
            //Revert to last locked state.
            getGrid();

            //Reset all cell sets.
            addAllValuesToSet();

            btn_verify.setEnabled(false);
            btn_reset.setEnabled(false);
        });

        btn_new.addActionListener(e -> {
            //Close current frame.
            mainframe.dispose();

            //Start again.
            new Solver();
        });

        btn_verify.addActionListener(e -> {
            //Rows and Columns
            boolean flag = true;
            for (int i = 0; i < size; ++i) {
                int[] cnt_r = new int[size];
                int[] cnt_c = new int[size];
                valueCounter(i, cnt_r, cnt_c);
                for (int x = 0; x < size; ++x) {
                    if (cnt_r[x] != 1 || cnt_c[x] != 1) {
                        flag = false;
                    }
                }
            }

            //Grid
            for (int i1 = 0; i1 < size / gridSize; ++i1) {
                for (int j1 = 0; j1 < size / gridSize; ++j1) {
                    int[] cnt = new int[size];
                    gridValueCounter(cnt, i1, j1);
                    for (int x = 0; x < size; ++x) {
                        if (cnt[x] != 1) {
                            flag = false;
                        }
                    }
                }
            }

            if (countConfirmed() < (size * size))
                flag = false;

            if (flag)
                JOptionPane.showMessageDialog(mainframe, "Success! Sudoku solved correctly!");
            else
                JOptionPane.showMessageDialog(mainframe, "Oh no! Sudoku not solved.");
        });
    }

    /**
     * Sets the font of a JButton.
     *
     * @param btn the button whose font is to be changed.
     */
    private void setButtonFont(JButton btn) {
        btn.setFont(new Font("DialogInput", Font.PLAIN, 20));
    }

    /**
     * Updates all cellSets to contain all possible values.
     */
    private void addAllValuesToSet() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                int k = 1;
                while (k <= size) {
                    cellSets.get(i).get(j).add(k++);
                }
            }
        }
        resetFlags();
    }

    /**
     * Updates the text of the sudoku grid buttons on the basis of the values stored the the grid[][] array.
     */
    private void getGrid() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (grid[i][j] != 0)
                    buttons[i][j].setText(Integer.toString(grid[i][j]));
                else
                    buttons[i][j].setText("");
            }
        }
    }

    /**
     * Allows us to add action listeners to handle a click on any of the sudoku buttons.
     *
     * @param button the button in question
     * @param row    the row index of the button
     * @param col    the column index of the button
     */
    private void addOnClickListener(JButton button, int row, int col) {
        button.addActionListener(e -> {
            mainframe.setEnabled(false);
            btn_reset.setEnabled(true);
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    buttons[i][j].setEnabled(false);
                }
            }
            JButton[] temp = new JButton[1];
            temp[0] = button;
            new Choice(temp, row, col, buttons, mainframe);
        });
    }

    /**
     * Checks if a given row/column is the last in any sub-grid on the sudoku grid.
     *
     * @param j the row/column index
     * @return the thickness of the border to be used.
     */
    private int isLast(int j) {
        if (j % gridSize == gridSize - 1) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Checks if a given row/column is the first in any sub-grid on the sudoku grid.
     *
     * @param j the row/column index
     * @return the thickness of the border to be used.
     */
    private int isFirst(int j) {
        if (j % gridSize == 0) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Updates the sets on the basis of the input provided to the sudoku cells on the GUI.
     */
    private void updateFromGrid() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (grid[i][j] != 0) {
                    cellSets.get(i).get(j).clear();
                    cellSets.get(i).get(j).add(grid[i][j]);
                }
            }
        }
    }

    /**
     * Uses the cells which are already confirmed to update sets of the cells in its row, column and grid.
     */
    private void updateFromConfirmed() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (cellSets.get(i).get(j).size() == 1 && !flag[i][j]) {
                    removeInstances(cellSets.get(i).get(j), i, j);
                    flag[i][j] = true;
                }
            }
        }
    }

    /**
     * Calculates the row position of a grid given the row index of a cell.
     *
     * @param row the row index of the cell
     * @return the row index of the grid
     */
    private int calculateGridRow(int row) {
        return row / gridSize;
    }

    /**
     * Calculates the column position of a grid given the column index of a cell.
     *
     * @param col the column index of the cell
     * @return the column index of the grid
     */
    private int calculateGridCol(int col) {
        return col / gridSize;
    }

    /**
     * Removes instances of a number from its row, column and grid.
     *
     * @param cellSet the set of the cell in question
     * @param row     the row index of the cell
     * @param col     the column index of the cell
     */
    private void removeInstances(HashSet<Integer> cellSet, int row, int col) {
        int gr = calculateGridRow(row);
        int gc = calculateGridCol(col);

        //Remove element from sets of that row
        for (int j = 0; j < size; ++j) {
            if (j != col) {
                cellSets.get(row).get(j).removeAll(cellSet);
            }
        }

        //Remove element from sets of that column
        for (int i = 0; i < size; ++i) {
            if (i != row) {
                cellSets.get(i).get(col).removeAll(cellSet);
            }
        }

        //Remove element from sets of that grid
        for (int i = gr * gridSize, cnt_r = 0; cnt_r < gridSize; ++i, ++cnt_r) {
            for (int j = gc * gridSize, cnt_c = 0; cnt_c < gridSize; ++j, ++cnt_c) {
                if (!(i == row && j == col)) {
                    cellSets.get(i).get(j).removeAll(cellSet);
                }
            }
        }
    }

    /**
     * Updates the sudoku grid with cell values that are confirmed.
     */
    private void publish() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (cellSets.get(i).get(j).size() == 1) {
                    String text = cellSets.get(i).get(j).toString();
                    buttons[i][j].setText(text.substring(1, text.length() - 1));
                }
            }
        }
        printSets();
    }

    /**
     * Counts the number of cells that are confirmed.
     *
     * @return the count
     */
    private int countConfirmed() {
        int cnt = 0;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (cellSets.get(i).get(j).size() == 1) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    /**
     * Counts the number of confirmed cells that have already been used to update other cells.
     *
     * @return the count
     */
    private int countFlags() {
        int cnt = 0;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (flag[i][j])
                    cnt++;
            }
        }
        return cnt;
    }

    /**
     * Traverses each row/column/grid to find if there exists a cell that has a candidate not possible anywhere else in that row/column/grid.
     */
    private void onlyChoice() {

        //Rows and Columns
        for (int i = 0; i < size; ++i) {
            int[] cnt_r = new int[size];
            int[] cnt_c = new int[size];
            valueCounter(i, cnt_r, cnt_c);
            for (int x = 0; x < size; ++x) {
                if (cnt_r[x] == 1 || cnt_c[x] == 1) {
                    for (int j = 0; j < size; ++j) {
                        onlyChoicePicker(j, cnt_r, x, i);
                        onlyChoicePicker(i, cnt_c, x, j);
                    }
                }
            }
        }

        //Grid
        for (int i1 = 0; i1 < size / gridSize; ++i1) {
            for (int j1 = 0; j1 < size / gridSize; ++j1) {
                int[] cnt = new int[size];
                gridValueCounter(cnt, i1, j1);
                for (int x = 0; x < size; ++x) {
                    if (cnt[x] == 1) {
                        for (int i = i1 * gridSize, cnt_i = 0; cnt_i < gridSize; ++i, ++cnt_i) {
                            for (int j = j1 * gridSize, cnt_j = 0; cnt_j < gridSize; ++j, ++cnt_j) {
                                onlyChoicePicker(j, cnt, x, i);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Fixes a value of a cell to the candidate that only that cell has in its row/column/grid.
     *
     * @param i     the starting row index
     * @param cnt_c the array of counters corresponding to all candidates
     * @param x     the index in the counter array which has been found
     * @param j     the starting column index
     */
    private void onlyChoicePicker(int i, int[] cnt_c, int x, int j) {
        if (cnt_c[x] == 1)
            if (cellSets.get(j).get(i).contains(x + 1)) {
                buttons[j][i].setText(Integer.toString(x + 1));
                cellSets.get(j).get(i).clear();
                cellSets.get(j).get(i).add(x + 1);
            }
    }

    /**
     * Function to solve the Sudoku grid; uses backtracking iff regular constraint propagation techniques are inadequate to find the solution.
     */
    private void solveWithBacktrack() {
        if (countConfirmed() < (size * size))
            solve();
        if (countConfirmed() < (size * size)) {
            ArrayList<ArrayList<HashSet<Integer>>> cellSetsSaved = new ArrayList<>();
            boolean[][] flagSaved = new boolean[size][size];
            String[][] textSaved = new String[size][size];
            int[][] gridSaved = new int[size][size];
            initialiseSet(cellSetsSaved);
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    cellSetsSaved.get(i).get(j).addAll(cellSets.get(i).get(j));
                    flagSaved[i][j] = flag[i][j];
                    textSaved[i][j] = buttons[i][j].getText();
                    gridSaved[i][j] = grid[i][j];
                }
            }
            makeAGuess(0);
            solve();
            if (checkIfFalse()) {
                backtrackCnt++;
                System.out.println(ConsoleColors.RED_BOLD + "\n\tBACKTRACK!" + ConsoleColors.RESET);
                for (int i = 0; i < size; ++i) {
                    for (int j = 0; j < size; ++j) {
                        cellSets.get(i).get(j).clear();
                        cellSets.get(i).get(j).addAll(cellSetsSaved.get(i).get(j));
                        flag[i][j] = flagSaved[i][j];
                        buttons[i][j].setText(textSaved[i][j]);
                        grid[i][j] = gridSaved[i][j];
                    }
                }
                makeAGuess(1);
                solve();
            }
            solveWithBacktrack();
        }
    }

    /**
     * Function that makes a guess in a cell that has only two possible values.
     *
     * @param type signifies whether a guess has already been made or not
     */
    private void makeAGuess(int type) {

        boolean flagDup = false;
        int l = 1, r = 2;
        if (type == 1) {
            l = 4;
            r = 5;
        }
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (cellSets.get(i).get(j).size() == 2 && flagBack[i][j] == type && !flagDup) {
                    String temp = cellSets.get(i).get(j).toString().substring(l, r);
                    cellSets.get(i).get(j).clear();
                    cellSets.get(i).get(j).add(Integer.parseInt(temp));
                    buttons[i][j].setText(temp);
                    grid[i][j] = Integer.parseInt(temp);
                    flagBack[i][j]++;
                    flagDup = true;
                    if (type == 0)
                        System.out.println(ConsoleColors.YELLOW_BOLD + "\n\tMaking a guess at cell (" + (i + 1) + "," + (j + 1) + ")" + ConsoleColors.RESET);
                    break;
                }
            }
        }
    }

    /**
     * Initialises sets.
     *
     * @param cellSetsSaved the list of lists of sets to be initialised
     */
    private void initialiseSet(ArrayList<ArrayList<HashSet<Integer>>> cellSetsSaved) {
        for (int i = 0; i < size; ++i) {
            cellSetsSaved.add(i, new ArrayList<>());
            for (int j = 0; j < size; ++j) {
                cellSetsSaved.get(i).add(j, new HashSet<>());
            }
        }
    }

    /**
     * Loops between eliminating choices from confirmed cells and filling in only choice as long as it can.
     */
    private void solve() {
        while (countFlags() < countConfirmed()) {
            updateFromConfirmed();
            publish();
            onlyChoice();
            publish();
            if (checkIfFalse())
                return;
        }
    }

    /**
     * Checks if the current state of the sudoku is invalid.
     *
     * @return true if the state is invalid
     */
    private boolean checkIfFalse() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (cellSets.get(i).get(j).size() == 0)
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks if number of entries is at least 16.
     *
     * @return true if number of entries greater than or equal to 16
     */
    private boolean checkIfEnough() {
        int cnt = 0;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (grid[i][j] != 0)
                    cnt++;
            }
        }
        return cnt >= 16;
    }

    /**
     * Counts the instances of all possible numbers in the range.
     *
     * @param i     the row/column index
     * @param cnt_r the counter array for a row
     * @param cnt_c the counter array for a column
     */
    private void valueCounter(int i, int[] cnt_r, int[] cnt_c) {
        for (int j = 0; j < size; ++j) {
            for (int k = 1; k <= size; ++k) {
                if (cellSets.get(i).get(j).contains(k)) {
                    cnt_r[k - 1]++;
                }
                if (cellSets.get(j).get(i).contains(k)) {
                    cnt_c[k - 1]++;
                }
            }
        }
    }

    /**
     * Counts the instances of all possible numbers in the grid.
     *
     * @param cnt the counter array
     * @param i1  starting row location of the grid
     * @param j1  starting column location of the grid
     */
    private void gridValueCounter(int[] cnt, int i1, int j1) {
        for (int i = i1 * gridSize, cnt_i = 0; cnt_i < gridSize; ++i, ++cnt_i) {
            for (int j = j1 * gridSize, cnt_j = 0; cnt_j < gridSize; ++j, ++cnt_j) {
                for (int k = 1; k <= size; ++k) {
                    if (cellSets.get(i).get(j).contains(k)) {
                        cnt[k - 1]++;
                    }
                }
            }
        }
    }

    /**
     * Resets all flags
     */
    private void resetFlags() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                flag[i][j] = false;
                flagBack[i][j] = 0;
            }
        }
    }

    /**
     * Print the contents of the sets on the console.
     */
    private void printSets() {
        boolean flag = false;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (!(cellSets.get(i).get(j).equals(cellSetsChange.get(i).get(j)))) {
                    flag = true;
                    break;
                }
            }
        }
        if (flag) {
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    cellSetsChange.get(i).get(j).clear();
                    cellSetsChange.get(i).get(j).addAll(cellSets.get(i).get(j));
                }
            }
            System.out.println(ConsoleColors.BLUE_UNDERLINED + "\nCurrent Status:" + ConsoleColors.RESET);
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    if (cellSets.get(i).get(j).size() == 0)
                        System.out.println(ConsoleColors.RED_BOLD + (i + 1) + "," + (j + 1) + " -> " + cellSets.get(i).get(j) + ConsoleColors.RESET);
                    else if (cellSets.get(i).get(j).size() == 1)
                        System.out.println(ConsoleColors.GREEN + (i + 1) + "," + (j + 1) + " -> " + cellSets.get(i).get(j) + ConsoleColors.RESET);
                    else
                        System.out.println((i + 1) + "," + (j + 1) + " -> " + cellSets.get(i).get(j));
                }
            }
        }
    }
}
