package solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

/**
 * Contains everything needed to represent a game of Sudoku and solve it.
 * Initial state is intended to be loaded from a file.
 * 
 * @author Jacob Knispel (https://github.com/knispeja)
 */
public class SudokuPuzzle {
	
	// All values in the current board; 0 indicates empty/undecided
	private int[][] board;
	
	private int boardSize; // dimension of the board, in cells
	private int partitionSize; // square root of the board size
	
	// Priority queue and its comparator to give an order to the solver
	private PriorityQueue<Cell> cells;
	private class CellComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell arg0, Cell arg1) {
			return Integer.compare(
					arg0.getInitialPossibleValueCount(), 
					arg1.getInitialPossibleValueCount()
				);
		}
	}
	
	// CellCluster arrays for quick generation of possible values
	private CellCluster[] rows;
	private CellCluster[] cols;
	private CellCluster[][] chunks;
	
	/**
	 * Construct a new puzzle object, loaded from the given file.
	 * @param file
	 */
	public SudokuPuzzle(File file) {    	
	    try {
	    	loadFromFile(file);
	    }
	    catch (FileNotFoundException exception) {
		    System.out.println("Input file not found: " + file.getName());
	    } 
	    catch (Exception e) {
	    	System.out.println("Malformed input file: " + e);
		}
	}
	
	/**
	 * Load the current object using the given file.
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void loadFromFile(File file) throws FileNotFoundException {
		// Initialize some important values based on board size
		Scanner input = new Scanner(file);
		this.boardSize = input.nextInt();
		this.partitionSize = (int) Math.sqrt(this.boardSize);
		this.board = new int[this.boardSize][this.boardSize];
		
		// Instantiate cell cluster arrays
		this.rows = new CellCluster[this.boardSize];
		this.cols = new CellCluster[this.boardSize];
		for(int i=0; i<this.boardSize; i++) {
			this.rows[i] = new CellCluster(this.boardSize);
			this.cols[i] = new CellCluster(this.boardSize);
		}
		this.chunks = new CellCluster[this.partitionSize][this.partitionSize];
		for(int i=0; i<this.partitionSize; i++)
			for(int j=0; j<this.partitionSize; j++)
				this.chunks[i][j] = new CellCluster(this.boardSize);
		
		// Loop through the input and fill out values
    	int i=0;
    	int j=0;
    	int count=0;
    	while(input.hasNext()){
    		int temp = input.nextInt();
    		count++;
    		if(temp != 0) {
	    		this.rows[i].putValue(temp);
	    		this.cols[j].putValue(temp);
	    		getChunkClusterFor(i, j).putValue(temp);
    		}
			this.board[i][j++] = temp;
			if(j == this.boardSize) {
				j = 0;
				i++;
			}
    	}
    	input.close();
    	
    	// Sanity check
    	if(count != this.boardSize*this.boardSize) throw new RuntimeException("Incorrect number of inputs.");
    	
    	// Preprocessing to fill priority queue of cells
    	this.cells = new PriorityQueue<Cell>(this.boardSize*this.boardSize, new CellComparator());
		for(int row=0; row<this.boardSize; row++) {
			CellCluster rowCluster = this.rows[row];
			for(int col=0; col<this.boardSize; col++) {
				if(this.board[row][col] != 0)
					continue;
				CellCluster colCluster = this.cols[col];
				CellCluster chunkCluster = getChunkClusterFor(row, col);
				Cell newCell = new Cell(row, col);
				newCell.setPossibleValues(rowCluster.intersectWith(colCluster, chunkCluster));
				this.cells.add(newCell);
			}
		}
	}
	
	/**
	 * QoL function for getting the chunk cluster for a given cell.
	 * @param row
	 * @param col
	 * @return
	 */
	private CellCluster getChunkClusterFor(int row, int col) {
		return this.chunks[Math.floorDiv(row, this.partitionSize)][Math.floorDiv(col, this.partitionSize)];
	}
	
	/**
	 * Recursively solves the loaded Sudoku problem.
	 * @return
	 */
	public boolean solve() {
		Cell cell; // Get the cell out of the PQ that we will be using
		if((cell = this.cells.poll()) != null) {
			CellCluster rowCluster = this.rows[cell.getRow()];
			CellCluster colCluster = this.cols[cell.getCol()];
			CellCluster chunkCluster = getChunkClusterFor(cell.getRow(), cell.getCol());
			
			// Get the possible value set or create it if necessary
			Set<Integer> possibleValues;
			if(rowCluster.hasUpdated() || colCluster.hasUpdated() || chunkCluster.hasUpdated()) {
				possibleValues = rowCluster.intersectWith(colCluster, chunkCluster);
				cell.setPossibleValues(possibleValues);
			} else {
				possibleValues = cell.getInitialPossibleValues();
			}
			rowCluster.setHasUpdated();
			colCluster.setHasUpdated();
			chunkCluster.setHasUpdated();
			
			// Loop through possible values and try them out
			for(int value: possibleValues) {
				this.board[cell.getRow()][cell.getCol()] = value;
				rowCluster.putValue(value);
				colCluster.putValue(value);
				chunkCluster.putValue(value);
				if(solve()) return true; // Recurse, if true is returned, we're good!
				this.board[cell.getRow()][cell.getCol()] = 0;
				rowCluster.removeValue(value);
				colCluster.removeValue(value);
				chunkCluster.removeValue(value);
			}
			this.cells.add(cell); // Put the cell back into the PQ
			return false; // No values worked, so impossible board state
		}
		return true; // No more cells left in the PQ, we're done!
	}
	
	@Override
	public String toString() {
		String output = this.boardSize + "\n";
		for(int i=0; i<this.boardSize; i++) {
			for(int j=0; j<this.boardSize; j++) {
				output+=this.board[i][j] + " ";
			}
			output = output.trim();
			output += "\n";
		}
		return output.trim();
	}
}
