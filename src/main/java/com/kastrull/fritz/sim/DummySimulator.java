package com.kastrull.fritz.sim;

public class DummySimulator implements Simulator {

	@Override
	public SimState simulate(SimState state) {

		return state;
	}
}
