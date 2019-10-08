import javax.swing.*;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class minesweeper extends JFrame {

   //assigned value of mine tiles
    public static final int MINE = 10;

    //number of mines = gridSize = POPULATION_CONSTANT
    private static double POPULATION_CONSTANT = 2;
    //x and y value of tiles in grid
    public int gridSize = 10;

    //fixed memory for Cell array
    public Cell[] reusableStorage = new Cell[8];
    
    static Cell[][] cells;

    
    public int mineNum;
    
    public void reset() {
        createMines();
            mineNum = 0;
    }

    private void resetAllCells() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reset();
            }
        }
    }

    public void createMines() {
        resetAllCells();

        final int    mineCount = (int) POPULATION_CONSTANT * gridSize;
        final Random random    = new Random();

        // Map all (row, col) pairs to unique integers
        Set<Integer> positions = new HashSet<>(gridSize * gridSize);
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                positions.add(row * gridSize + col);
            }
        }

        // Initialize mines
        for (int index = 0; index < mineCount; index++) {
            int choice = random.nextInt(positions.size());
            int row    = choice / gridSize;
            int col    = choice % gridSize;
            cells[row][col].setValue(MINE);
            mineNum++;
            //Counter.setText(String.valueOf(mineNum));
            positions.remove(choice);
        }

        // Initialize neighbour counts
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (!cells[row][col].isAMine()) {
                    cells[row][col].updateNeighbourCount();
                }
            }
        }
    }

   public void handleCell(Cell cell) {
      if (cell.isAMine()) {
         cell.setForeground(Color.RED);
         cell.reveal();
         view viewVar = new view();
         viewVar.gameOver("Game Over");
         return;
      }
      if (cell.getValue() == 0) {
            Set<Cell> positions = new HashSet<>();
            positions.add(cell);
            cascade(positions);
         } else {
            cell.reveal();
         }
         checkForWin();
    }
    
    private void checkForWin() {
        boolean won = true;
        outer:
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                if (!cell.isAMine() && cell.isEnabled()) {
                    won = false;
                    break outer;
                }
            }
        }

        if (won) {
            view viewVar = new view();
            viewVar.win();
        }
     }
     /*
       public void revealBoardAndDisplay(String message) {
        mineNum = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reveal();
            }
        }
        view.gameOver();
        createMines();
    }
    */


    private void cascade(Set<Cell> positionsToClear) {
        while (!positionsToClear.isEmpty()) {
            // Set does not have a clean way for retrieving
            // a single element. This is the best way I could think of.
            Cell cell = positionsToClear.iterator().next();
            positionsToClear.remove(cell);
            cell.reveal();

            cell.getNeighbours(reusableStorage);
            for (Cell neighbour : reusableStorage) {
                if (neighbour == null) {
                    break;
                }
                if (neighbour.getValue() == 0
                    && neighbour.isEnabled()) {
                    positionsToClear.add(neighbour);
                } else {
                    neighbour.reveal();
                }
            }
        }
    }

    private void run(final int gridSize) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) { }
        // Launch the program
        this.gridSize = gridSize;
        cells = new Cell[gridSize][gridSize];
        view viewVar = new view();
        viewVar.runview(gridSize);
    }

    public static void main(String[] args) {
        final int gridSize = 10;
        minesweeper runVar = new minesweeper();
        SwingUtilities.invokeLater(() -> runVar.run(gridSize));
    }
}