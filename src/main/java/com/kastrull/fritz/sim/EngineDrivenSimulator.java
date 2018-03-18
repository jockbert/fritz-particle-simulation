package com.kastrull.fritz.sim;

import com.kastrull.fritz.physics.LinearPhysics;
import com.kastrull.fritz.physics.Physics;

public class EngineDrivenSimulator implements Simulator {

	private static Physics PHY = new LinearPhysics();

	public EngineDrivenSimulator() {
	}

	@Override
	public SimState simulate(SimState state) {

		state = state.currentTime(state.targetTime());

		return state;
	}
}
