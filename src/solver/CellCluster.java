package solver;

import java.util.HashSet;
import java.util.Set;

public class CellCluster {
	
	private Set<Integer> remainingValues;
	private boolean hasUpdated;
	
	public CellCluster(int boardSize) {
		this.remainingValues = new HashSet<Integer>();
		for(int i=1; i<=boardSize; i++)
			this.remainingValues.add(i);
		this.hasUpdated = false;
	}
	
	/**
	 * Place this value into the cluster (hence removing it from the remaining values).
	 * @param value
	 */
	public void putValue(int value) {
		this.remainingValues.remove(value);
	}
	
	/**
	 * Remove this value from the cluster (hence adding it to the remaining values).
	 * @param value
	 */
	public void removeValue(int value) {
		this.remainingValues.add(value);
	}
	
	/**
	 * Returns the intersection of this cluster's remaining values with those given.
	 * Somewhat of a performance bottleneck: one set must be cloned to serve as a basis.
	 * I deliberately choose the smallest set, which provided some performance benefits.
	 * @param other1
	 * @param other2
	 * @return
	 */
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
	
	/**
	 * Returns true if this cluster is full of values currently.
	 * @return
	 */
	public boolean isComplete() {
		return this.remainingValues.size() == 0;
	}
	
	/**
	 * Notify that the cluster has been updated since initialization.
	 */
	public void setHasUpdated() {
		this.hasUpdated = true;
	}
	
	/**
	 * Returns true if this cluster has been updated since initialization.
	 * @return
	 */
	public boolean hasUpdated() {
		return this.hasUpdated;
	}
}
