package com.kastrull.fritz;

import com.kastrull.fritz.sim.DummySimulator;
import com.kastrull.fritz.sim.SimState;
import com.kastrull.fritz.sim.Simulator;

public class Main {

	public static void main(String[] args) {

		println("Fritz particle simulator");
		println("========================");
		println("");

		Simulator sim = new DummySimulator();

		double upToTime = 100;
		SimState state = SimState.createWithWalls(10.0, 10.0).addParticle(0.0, 0.0, 0.0, 0.0).targetTime(upToTime);

		SimState result = sim.simulate(state);

		double isAtTime = result.currentTime();
		boolean isComplete = result.targetTime() == isAtTime;
		double wallAbsorbedMomentum = result.wallAbsorbedMomentum();

		println("Target time ..............: " + upToTime);
		println("Is at time ...............: " + isAtTime);
		println("Is complete ..............: " + isComplete);
		println("Wall-absorbed momentum ...: " + wallAbsorbedMomentum);
	}

	private static void println(String line) {
		System.out.println(line); // NOSONAR
	}
}
