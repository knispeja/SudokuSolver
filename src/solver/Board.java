package solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

public class Board {

	private int[][] boardVals;
	private CellCluster[] rows;
	private CellCluster[] cols;
	private CellCluster[][] chunks;
	
	private int boardSize;
	private int partitionSize;
	
	public Board(File file) {    	
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
	
	public void loadFromFile(File file) throws FileNotFoundException {
		// Initialize some important values based on board size
		Scanner input = new Scanner(file);
		this.boardSize = input.nextInt();
		this.partitionSize = (int) Math.sqrt(this.boardSize);
		this.boardVals = new int[this.boardSize][this.boardSize];
		
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
    		addValueToClusters(temp, i, j);
			this.boardVals[i][j++] = temp;
			if(j == this.boardSize) {
				j = 0;
				i++;
			}
    	}
    	input.close();
    	
    	// Sanity check
    	if(count != this.boardSize*this.boardSize) throw new RuntimeException("Incorrect number of inputs.");
	}
	
	public void addValueToClusters(int value, int row, int col) {
		if(value == 0) return;
		this.rows[row].putValue(value);
		this.cols[col].putValue(value);
		getChunkClusterFor(row, col).putValue(value);
	}
	
	public void removeValueFromClusters(int value, int row, int col) {
		if(value == 0) return;
		this.rows[row].removeValue(value);
		this.cols[col].removeValue(value);
		getChunkClusterFor(row, col).removeValue(value);
	}
	
	private CellCluster getChunkClusterFor(int row, int col) {
		return this.chunks[Math.floorDiv(row, this.partitionSize)][Math.floorDiv(col, this.partitionSize)];
	}
	
	public boolean solve() {
		for(int row=0; row<this.boardSize; row++) {
			CellCluster rowCluster = this.rows[row];
			if(rowCluster.isComplete())
				continue;
			for(int col=0; col<this.boardSize; col++) {
				
				if(this.boardVals[row][col] != 0)
					continue;
				
				CellCluster colCluster = this.cols[col];
				CellCluster chunkCluster = getChunkClusterFor(row, col);
				Set<Integer> possibleValues = rowCluster.intersectWith(colCluster, chunkCluster);
				if(possibleValues.isEmpty())
					return false;
				for(int value: possibleValues) {
					this.boardVals[row][col] = value;
					addValueToClusters(value, row, col);
					if(solve()) return true;
					this.boardVals[row][col] = 0;
					removeValueFromClusters(value, row, col);
				}
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		String output = this.boardSize + "\n";
		for(int i=0; i<this.boardSize; i++) {
			for(int j=0; j<this.boardSize; j++) {
				output+=this.boardVals[i][j] + " ";
			}
			output = output.trim();
			output += "\n";
		}
		return output.trim();
	}
}