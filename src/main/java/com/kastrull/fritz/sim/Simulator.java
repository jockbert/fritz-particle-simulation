package com.kastrull.fritz.sim;

public interface Simulator {

	SimCalculation simulate(SimState state, double upToTime);

}
