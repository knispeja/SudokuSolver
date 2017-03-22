package solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * A simple Sudoku solver.
 * 
 * @author Jacob Knispel (https://github.com/knispeja)
 */
public class SudokuSolver {
	
	private static final String SOLUTION_FILE_SUFFIX = "Solution";
	
	public static void main(String[] args){
		
		// Input validation
		if(args.length < 1 || args[0] == null || args[0].isEmpty()) {
			System.err.println("Please provide a Sudoku file as the first argument.");
			return;
		}

		// Create board from file and display input
		SudokuPuzzle puzzle = new SudokuPuzzle(new File(args[0]));
		System.out.println("Input:");
		System.out.println(puzzle);
		
		// Measure time elapsed
		long tStart = System.currentTimeMillis();
		
		// Solve the board
		boolean solved = puzzle.solve();
		
		// Record time elapsed
		long tEnd = System.currentTimeMillis();
		long elapsedMs = tEnd - tStart;	
		
		// Display results
		System.out.println();
		String result;
		if(solved) {
			System.out.println("Output:");
			result = puzzle.toString();
		} else {
			result = "[Impossible Puzzle]";
		}
		System.out.println(result);
		System.out.println("Time elapsed: " + elapsedMs + "ms");
		
		// Write result to a file
		int dotIndex = args[0].lastIndexOf('.');
		String newFileName = "";
		if(dotIndex != -1) {
			String filePrefix = args[0].substring(0, dotIndex);
			String fileSuffix = args[0].substring(dotIndex);
			newFileName = filePrefix + SOLUTION_FILE_SUFFIX + fileSuffix;
		} else {
			newFileName = args[0] + SOLUTION_FILE_SUFFIX;
		}
		try {
			PrintWriter out = new PrintWriter(newFileName);
			out.print(puzzle);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		}
	}
}
