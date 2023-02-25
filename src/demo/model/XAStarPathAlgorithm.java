package demo.model;

import graph.matrix.AStarCostEvaluator.EvaluatorDisabledException;
import graph.model.AStarPathAlgorithm;
import graph.model.INode;

public class XAStarPathAlgorithm extends AStarPathAlgorithm {

	private boolean searching;

	@Override
	public boolean searchPath(INode start, INode end) {
		searching = true;
		boolean value = false;
		try {
			value = super.searchPath(start, end);
		}
		catch (EvaluatorDisabledException e) {
			// nothing wrong, just to terminate the process
		}
		searching = false;
		return value;
	}

	public boolean isSearching() {
		return searching;
	}

}
