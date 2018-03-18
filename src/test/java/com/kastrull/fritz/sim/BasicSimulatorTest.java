package com.kastrull.fritz.sim;

import com.kastrull.fritz.physics.LinearPhysics;
import com.kastrull.fritz.primitives.WithAssert;

public class BasicSimulatorTest extends SimulatorTest implements WithAssert {

	@Override
	protected Simulator cut() {
		return new BasicSimulator(new LinearPhysics());
	}
}
