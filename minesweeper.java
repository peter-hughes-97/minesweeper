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
    private static final int MINE = 10;
    // The size in pixels for the frame.
    private static final int SIZE = 500;

    // The number of mines at generated is the grid size * this constant
    private static double POPULATION_CONSTANT = 3;

    // This fixed amount of memory is to avoid repeatedly declaring
    // new arrays every time a cell's neighbours are to be retrieved.
    private static Cell[] reusableStorage = new Cell[8];

    private int gridSize;
    
    int mineNum;

    private Cell[][] cells;

    private JLabel Counter;
    private JLabel clock;
    private JFrame  frame;
    private JButton reset;
    
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
    
    private final ActionListener actionListener = actionEvent -> {
        Object source = actionEvent.getSource();
        if (source == reset) {
            createMines();
            mineNum = 0;
        } else {
            handleCell((Cell) source);
        }
    };

    private class Cell extends JButton {
        private final int row;
        private final int col;
        private       int value;

        Cell(final int row, final int col,
             final ActionListener actionListener) {
            this.row = row;
            this.col = col;
            addActionListener(actionListener);
            setText("");
        }

        int getValue() {
            return value;
        }

        void setValue(int value) {
            this.value = value;
        }

        boolean isAMine() {
            return value == MINE;
        }

        void reset() {
            setValue(0);
            setEnabled(true);
            setText("");
            mineNum = 0;
        }

        void reveal() {
            setEnabled(false);
            setText(isAMine() ? "X" : String.valueOf(value));
        }

        void updateNeighbourCount() {
            getNeighbours(reusableStorage);
            for (Cell neighbour : reusableStorage) {
                if (neighbour == null) {
                    break;
                }
                if (neighbour.isAMine()) {
                    value++;
                }
            }
        }

        void getNeighbours(final Cell[] container) {
            // Empty all elements first
            for (int i = 0; i < reusableStorage.length; i++) {
                reusableStorage[i] = null;
            }

            int index = 0;

            for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                for (int colOffset = -1; colOffset <= 1; colOffset++) {
                    // Make sure that we don't count ourselves
                    if (rowOffset == 0 && colOffset == 0) {
                        continue;
                    }
                    int rowValue = row + rowOffset;
                    int colValue = col + colOffset;

                    if (rowValue < 0 || rowValue >= gridSize
                        || colValue < 0 || colValue >= gridSize) {
                        continue;
                    }

                    container[index++] = cells[rowValue][colValue];
                }
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Cell cell = (Cell) obj;
            return row == cell.row &&
                   col == cell.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    private minesweeper(final int gridSize) {
        this.gridSize = gridSize;
        cells = new Cell[gridSize][gridSize];

        frame = new JFrame("Minesweeper");
        frame.setSize(SIZE, SIZE);
        frame.setLayout(new BorderLayout());

        initializeButtonPanel();
        initializeGrid();
        createMenuBar();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    private void createMenuBar() {

        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        var eMenuItem = new JMenuItem("Exit");
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener((event) -> System.exit(0));
        
        
        var MineSetting = new JMenu("Mines");
        MineSetting.setMnemonic(KeyEvent.VK_M);
        
        var tenMenuItem = new JMenuItem("10 Mines");
        tenMenuItem.addActionListener(mineReset(2));
        
        var twMenuItem = new JMenuItem("20 Mines");
        tenMenuItem.addActionListener(mineReset(2));
        
        var thMenuItem = new JMenuItem("30 Mines");
        tenMenuItem.addActionListener(mineReset(2));

        fileMenu.add(eMenuItem);
        MineSetting.add(tenMenuItem);
        MineSetting.add(twMenuItem);
        MineSetting.add(thMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(MineSetting);

        frame.setJMenuBar(menuBar);
    }

    private void initializeButtonPanel() {
        JPanel buttonPanel = new JPanel();

        Counter = new JLabel (String.valueOf(mineNum));
        reset = new JButton("Reset");
        clock = new JLabel(String.valueOf(0));

        reset.addActionListener(actionListener);

        buttonPanel.add(Counter);
        buttonPanel.add(reset);
        buttonPanel.add(clock);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeGrid() {
      Color bg = new Color(74, 216, 255);
        Container grid = new Container();
        grid.setLayout(new GridLayout(gridSize, gridSize));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col] = new Cell(row, col, actionListener);
                grid.add(cells[row][col]);
                cells[row][col].setBackground(bg);
            }
        }
        createMines();
        frame.add(grid, BorderLayout.CENTER);
    }
    
    private final ActionListener mineReset(int n)  {
        POPULATION_CONSTANT = n;
        mineNum = 0;
        createMines();
        return null;
    };

    private void resetAllCells() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reset();
            }
        }
    }

    private void createMines() {
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
            Counter.setText(String.valueOf(mineNum));
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

   private void handleCell(Cell cell) {
      if (cell.isAMine()) {
         cell.setForeground(Color.RED);
         cell.reveal();
         revealBoardAndDisplay("Game Over");
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

    private void revealBoardAndDisplay(String message) {
        mineNum = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                cells[row][col].reveal();
            }
        }

        JOptionPane.showMessageDialog(
                frame, message, "Game Over",
                JOptionPane.ERROR_MESSAGE
        );

        createMines();
    }

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
            JOptionPane.showMessageDialog(
                    frame, "You have won!", "Congratulations",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private static void run(final int gridSize) {
        try {
            // Totally optional. But this applies the look and
            // feel for the current OS to the a application,
            // making it look native.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) { }
        // Launch the program
        new minesweeper(gridSize);
    }

    public static void main(String[] args) {
        final int gridSize = 10;
        SwingUtilities.invokeLater(() -> minesweeper.run(gridSize));
    }
}