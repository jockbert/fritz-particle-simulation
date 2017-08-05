package com.kastrull.fritz.sim;

public final class SimStateGenerator {

	private static final double START_TIME = 0.0;

	public static SimStateGenerator create(SimSetup setup) {
		return new SimStateGenerator(setup);
	}

	private final SimSetup setup;

	public SimStateGenerator(SimSetup setup) {
		this.setup = setup;
	}

	public SimState generateState() {
		return SimState
			.createWithWalls(setup.size.x, setup.size.y)
			.currentTime(START_TIME)
			.targetTime(setup.simTime);
	}
}
