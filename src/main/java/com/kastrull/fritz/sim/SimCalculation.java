package com.kastrull.fritz.sim;


public interface SimCalculation  {

	double isAtTime();

	boolean isComplete();

	SimState resultingState();

}
