Sudoku Solver Overview

Jacob Knispel - 3/21/2017

---

I was able to solve sudoku16ReallyHard.txt -- the solution my code generated is contained in "sudoku16ReallyHardSolution.txt".
The time required to solve sudoku16ReallyHard.txt is typically around 950ms, almost a second.
However, my code doesn't seem to solve Sudoku25.txt in a reasonable amount of time (<15min or so).

In order to speed up my solver for large puzzles like this one, I did a lot of preprocessing.
Initially, I create "CellCluster" objects for each conjoined portion of the puzzle.
In other words, there will be an object for each row, column, and NxN "chunk" of spaces, where N is the partition dimension.
Each CellCluster contains a set of unused values within that cluster.

I then create "Cell" objects which are put into a PriorityQueue to give the order in which to choose values for cells.
The PriorityQueue is ordered by which cells have the fewest possible values to the most possible.
This change had a much larger runtime impact than the "CellCluster" change -- it brought me from about 30m to 1s on the 16x16.

When I do my backtracking, I simply locate the three CellClusters associated with cells of value 0, then do a set intersection.*
The intersection of the three sets tells me all of the possible values for the current cell, without having to look up many values.

These efforts sped up my code greatly, with no apparent downsides other than a slightly increased memory usage.

On a final note, this could be sped up much more if the whole program was written in one class, and the 2D array was brought down
a dimension to a 1D one. This is because Java does not implicitly support 2D arrays, and so it actually takes multiple data accesses
to use one.

---

*For this reason, this would likely be faster in a scripting language that deals well with sets like Python. I deliberately
	clone the smallest set available (in CellCluster.intersectWith()) to start the set intersection, which provided a small 
	performance increase.