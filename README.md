# Sudoku
 <strong>This project solves any given Sudoku.</strong>
 <br><br>It accepts manual input or that in the form of a CSV file.
 <br>You could also use a mixture of both.
 <br>A minimum of 16 input values are required.
 <br>We first use <strong>Constraint Propagation</strong> to solve as much of the Sudoku as we can.
 <br>The <strong>heuristic</strong> used here is the number of remaining candidates in a cell.
 <br>The solver first runs a function that uses the cells currently in the grid to eliminate candidates from its corresponding row, column and sub-grid.
 <br>Lone candidates are filled into the grid.
 <br>It then checks for every row, column and sub-grid to see if there exists any candidate that is present at only one cell location within the row/column/sub-grid.
 <br>If that is the case, the cell is filled in with that candidate.
 <br>These two functions are looped together as long as they are able to cause any movement in the status.
 <br>
 <br>Once constraint propagation comes to a standstill, we move to the <strong>Backtracking</strong> approach <u>if necessary</u>.
 <br>Here, a cell with only two possible candidates is identified and one of them is assumed to be true.
 <br>With this assumption, we repeat all the steps till now (including constraint propagation first).
 <br>If the assumption turns out to be incorrect, we backtrack and choose the other candidate and re-do the whole thing.
 <br>It is efficiently able to handle multi level backtracking and the algorithm is far more 'intelligent' than any pure backtracking approach.
 <br>The solving speed can be varied using the argument in the <em>Thread.sleep()</em> method. It enable us to view the intermediate steps, especially for backtracking.
 <br>
 <br>Colour coded comprehensive logs are printed at every step of the way.
 <br>
 <br><strong>Button functionality:</strong>
 <ul>
 <li>FILE INPUT - Opens the File chooser and allows the user to select a single CSV file for input.</li>
 <li>EDIT - Enables the grid buttons; allows edits to the grid.</li>
 <li>LOCK - Disables edits to the grid and saves a copy of the current state.</li>
 <li>SOLVE - Solves the Sudoku using Constraint Propagation and Backtracking.</li>
 <li>RESET - Resets the grid to the status at the last 'lock'.</li>
 <li>NEW GRID - Starts everything over.</li>
 <li>VERIFY - Effectively verifies whether the Sudoku has been solved correctly or not.</li></ul>
