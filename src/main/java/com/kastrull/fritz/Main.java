package com.kastrull.fritz;

import com.kastrull.fritz.sim.DummySimulator;
import com.kastrull.fritz.sim.SimCalculation;
import com.kastrull.fritz.sim.SimState;
import com.kastrull.fritz.sim.Simulator;

public class Main {

	public static void main(String[] args) {

		println("Fritz particle simulator");
		println("========================");
		println("");

		Simulator sim = new DummySimulator();

		double upToTime = 100;
		SimState state = SimState.create(10.0, 10.0).addParticle(0.0, 0.0, 0.0, 0.0);

		SimCalculation simCalc = sim.simulate(state, upToTime);

		double isAtTime = simCalc.isAtTime();
		boolean isComplete = simCalc.isComplete();

		SimState finalState = simCalc.resultingState();

		println("Target time ..............: " + upToTime);
		println("Is at time ...............: " + isAtTime);
		println("Is complete ..............: " + isComplete);
		println("Wall-absorbed momentum ...: " + finalState.wallAbsorbedMomentum());
	}

	private static void println(String line) {
		System.out.println(line);
	}
}
