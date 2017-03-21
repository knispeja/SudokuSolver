package solver;

import java.util.HashSet;
import java.util.Set;

public class CellCluster {
	
	private Set<Integer> remainingValues;
	
	public CellCluster(int boardSize) {
		this.remainingValues = new HashSet<Integer>();
		for(int i=1; i<=boardSize; i++)
			this.remainingValues.add(i);
	}
	
	public void putValue(int value) {
		this.remainingValues.remove(value);
	}
	
	public void removeValue(int value) {
		this.remainingValues.add(value);
	}
	
	public Set<Integer> intersectWith(CellCluster other1, CellCluster other2) {
		Set<Integer> intersection;
		boolean other1This = other1.remainingValues.size() < this.remainingValues.size();
		boolean other2This = other2.remainingValues.size() < this.remainingValues.size();
		boolean other1Other2 = other1.remainingValues.size() < other2.remainingValues.size();
		if(other1This && other1Other2) {
			intersection = new HashSet<Integer>(other1.remainingValues);
			intersection.retainAll(this.remainingValues);
			intersection.retainAll(other2.remainingValues);
		} else if(other2This && !other1Other2) {
			intersection = new HashSet<Integer>(other2.remainingValues);
			intersection.retainAll(this.remainingValues);
			intersection.retainAll(other1.remainingValues);
		} else {
			intersection = new HashSet<Integer>(this.remainingValues);
			intersection.retainAll(other1.remainingValues);
			intersection.retainAll(other2.remainingValues);
		}
		return intersection;
	}
	
	public boolean isComplete() {
		return this.remainingValues.size() == 0;
	}
}
