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
import javax.swing.Timer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class minesweeper extends JFrame {
//implements MouseListener {
   
   //public void mousePressed(MouseEvent e){}
   //public void mouseEntered(MouseEvent e){}
   //public void mouseExited(MouseEvent e){}
   //public void mouseReleased(MouseEvent e){}
   
    // The value assigned to cells marked as mines. 10 works
    // because no cell will have more than 8 neighbouring mines.
    public static final int MINE = 10;

    // The number of mines at generated is the grid size * this constant
    private static double POPULATION_CONSTANT = 2;

    // This fixed amount of memory is to avoid repeatedly declaring
    // new arrays every time a cell's neighbours are to be retrieved.
    public Cell[] reusableStorage = new Cell[8];
    
    static Cell[][] cells;
    
    public int gridSize = 10;

    
    public int mineNum;
    
    //Timer timer = new Timer();
    //timer.setInitialDelay(pause);
    //timer.start(); 
    
    /*javax.swing.Timer t = new javax.swing.Timer(1000, new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String seconds = t.format("mmss");
            clock.setText(String.valueOf(seconds));
              //clock.setText(String.valueOf(t));
          }
       });*/
    
    
    
    public void reset() {
        createMines();
            mineNum = 0;
    }

    
    /*
    public final ActionListener mineReset(int n)  {
        POPULATION_CONSTANT = n;
        mineNum = 0;
        createMines();
        return null;
    };
    */

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
            // Totally optional. But this applies the look and
            // feel for the current OS to the a application,
            // making it look native.
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