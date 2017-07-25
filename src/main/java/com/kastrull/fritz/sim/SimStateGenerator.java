package com.kastrull.fritz.sim;

public final class SimStateGenerator {

	public static SimStateGenerator create() {
		// TODO Auto-generated method stub
		return new SimStateGenerator();
	}

	public SimState generateSimState(SimSetup setup) {
		return SimState.create(0, 0);
	};

}
