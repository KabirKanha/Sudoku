/*
This project solves any given Sudoku. It accepts manual input or that in the form of a CSV file.
You could also use a mixture of both.
The solver first runs a function that uses the cells currently in the grid to eliminate candidates from its corresponding row, column and sub-grid.
Lone candidates are filled into the grid.
It then checks for every row, column and sub-grid to see if there exists any candidate that is present at only one cell location within the row/column/sub-grid.
If that is the case, the cell is filled in with that candidate.
These two functions are looped together as long as they are able to cause any movement in the status.

Once constraint propagation comes to a standstill, we move to the backtracking approach if necessary.
Here, a cell with only two possible candidates is identified and one of them is assumed to be true.
With this assumption, we repeat all the steps till now (including constraint propagation first).
If the assumption turns out to be incorrect, we backtrack and choose the other candidate and re-do the whole thing.
It is efficiently able to handle multi level backtracking and the algorithm is far more 'intelligent' than any pure backtracking approach.
The solving speed can be varied using the argument in the Thread.sleep() method. It enable us to view the intermediate steps, especially for backtracking.

Author: Kabir Kanha Arora
Course: CSD 311 - Artificial Intelligence
Date: 09-Sep-19
 */

package com.kabirkanha.sudoku;
