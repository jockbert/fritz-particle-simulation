package com.kastrull.fritz.sim;

public class EngineDrivenSimulatorTest extends SimulatorTest {
	@Override
	protected Simulator cut() {
		return new EngineDrivenSimulator();
	}
}
