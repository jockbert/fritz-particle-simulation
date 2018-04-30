package com.kastrull.fritz.sim;

import com.kastrull.fritz.physics.LinearPhysics;

public class EngineDrivenSimulatorTest extends SimulatorTest {
	@Override
	protected Simulator cut() {
		return new EngineDrivenSimulator(new LinearPhysics());
	}
}
