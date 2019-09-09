package com.kabirkanha.sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * This class creates the dialog box to choose the value in case of a manual input.
 *
 * @author Kabir Kanha Arora
 */
class Choice {
    /**
     * Creates a new JFrame for the choice buttons.
     */
    private JFrame frameChoice = new JFrame();

    /**
     * Stores the user's choice.
     */
    private String choice = "";

    /**
     * Constructor for the Choice class
     *
     * @param temp      the button in question; passed as the only element of an array to make the call by reference.
     * @param row       the row in which the button lies.
     * @param col       the column in which the button lies.
     * @param buttons   the 2D array of buttons in the Sudoku grid.
     * @param mainframe the JFrame in which the Sudoku grid is placed.
     */
    Choice(JButton[] temp, int row, int col, JButton[][] buttons, JFrame mainframe) {

        //Declaring the JLabels for the choice frame.
        JLabel labelChoice1 = new JLabel("Pick the number");
        JLabel labelChoice2 = new JLabel("you want in the cell:");

        //Setting the visual appearance of the JLabels.
        labelChoice1.setFont(new Font("DialogInput", Font.PLAIN, 18));
        labelChoice1.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelChoice2.setFont(new Font("DialogInput", Font.PLAIN, 18));
        labelChoice2.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Declaring the JPanels
        JPanel panelChoice = new JPanel();
        JPanel mainPanelChoice = new JPanel();

        //Setting their layouts.
        panelChoice.setLayout(new GridLayout(Solver.gridSize, Solver.gridSize, 5, 5));
        mainPanelChoice.setLayout(new BoxLayout(mainPanelChoice, BoxLayout.PAGE_AXIS));

        //The 2D array of buttons for each choice of number.
        JButton[] numbers = new JButton[Solver.size * Solver.size];
        for (int i = 1; i <= Solver.size; ++i) {
            //Initialising all the buttons.
            numbers[i] = new JButton();

            //Setting their visual appearance.
            numbers[i].setText(Integer.toString(i));
            numbers[i].setFont(new Font("DialogInput", Font.BOLD, 18));
            numbers[i].setPreferredSize(new Dimension(25, 50));

            //Adding action listeners to each button.
            addOnClickListener(numbers[i], temp, buttons, mainframe);

            //Adding the buttons to the choice panel.
            panelChoice.add(numbers[i]);
        }

        //Adding the components to the panels.
        mainPanelChoice.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanelChoice.add(labelChoice1);
        mainPanelChoice.add(labelChoice2);
        mainPanelChoice.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanelChoice.add(panelChoice);
        mainPanelChoice.add(Box.createRigidArea(new Dimension(0, 20)));

        //Adding the main panel to the frame.
        frameChoice.add(mainPanelChoice);

        //Setting the visual appearance of the frame.
        frameChoice.setTitle("Number Picker for (" + (row + 1) + "," + (col + 1) + ")");
        frameChoice.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameChoice.setLocationRelativeTo(mainframe);
        frameChoice.pack();
        frameChoice.setVisible(true);

        //Adding a WindowListener to the frame to fire closeFunction() if the user closes the window.
        frameChoice.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                //Do nothing
            }

            /**
             * Actions to be performed when the window is closing.
             * @param e a WindowEvent
             */
            @Override
            public void windowClosing(WindowEvent e) {
                closeFunction(buttons, mainframe);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                //Do nothing
            }

            @Override
            public void windowIconified(WindowEvent e) {
                //Do nothing
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                //Do nothing
            }

            @Override
            public void windowActivated(WindowEvent e) {
                //Do nothing
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                //Do nothing
            }
        });
    }

    /**
     * Allows us to add action listeners to handle a click on any of the choice buttons.
     *
     * @param button    the button which was clicked
     * @param temp      the sudoku cell in question; passed as the only element of an array to make the call by reference.
     * @param buttons   the 2D array of buttons corresponding to the sudoku cells
     * @param mainframe the frame in which the sudoku is placed
     */
    private void addOnClickListener(JButton button, JButton[] temp, JButton[][] buttons, JFrame mainframe) {
        button.addActionListener(e -> {
            //Set text of the selected choice button to the button from the sudoku grid.
            choice += button.getText();
            temp[0].setText(choice);

            closeFunction(buttons, mainframe);

            //Close this frame.
            frameChoice.dispose();
        });
    }

    /**
     * Enables all the buttons in the sudoku grid
     *
     * @param buttons the 2D array of buttons corresponding to the sudoku cells
     */
    static void enableButtons(JButton[][] buttons) {
        for (int i = 0; i < Solver.size; ++i) {
            for (int j = 0; j < Solver.size; ++j) {
                buttons[i][j].setEnabled(true);
            }
        }
    }

    /**
     * Function that enables all the sudoku buttons as well as the frame containing the grid.
     *
     * @param buttons   the 2D array of buttons corresponding to the sudoku cells
     * @param mainframe the frame in which the sudoku is placed
     */
    private static void closeFunction(JButton[][] buttons, JFrame mainframe) {
        enableButtons(buttons);
        mainframe.setEnabled(true);
    }
}
