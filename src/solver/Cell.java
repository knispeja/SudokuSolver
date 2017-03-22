package solver;

import java.util.Set;

public class Cell {
	private Set<Integer> initialPossibleValues;
	private int row;
	private int col;
	
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public void setPossibleValues(Set<Integer> possibleValues) {
		this.initialPossibleValues = possibleValues;
	}
	
	public Set<Integer> getInitialPossibleValues() {
		return this.initialPossibleValues;
	}
	
	public int getInitialPossibleValueCount() {
		return this.initialPossibleValues.size();
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}
}
