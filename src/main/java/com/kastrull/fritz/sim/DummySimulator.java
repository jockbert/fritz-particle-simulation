package com.kastrull.fritz.sim;

public class DummySimulator implements Simulator {

	@Override
	public SimCalculation simulate(SimState state, double upToTime) {

		return new SimCalculation() {

			@Override
			public boolean isComplete() {
				return true;
			}

			@Override
			public double isAtTime() {
				return upToTime;
			}

			@Override
			public SimState resultingState() {
				return state;
			}
		};
	}
}
