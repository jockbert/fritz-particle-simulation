package com.kastrull.fritz.sim;

import java.util.List;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Particle;

public class SimStateGeneratorTest implements WithQuickTheories, WithSimSources {

	@Test
	@SuppressWarnings("unused")
	public void use() {
		SimStateGenerator generator = SimStateGenerator.create(SimSetup.NULL);
		SimState state = generator.generateState();

		List<Border> walls = state.walls();
		List<Particle> particles = state.particles();

		double currentTime = state.currentTime();
		double targetTime = state.targetTime();
		double wallAbsorbedMomentum = state.wallAbsorbedMomentum();
	}

	@Test
	public void currentTimeStartsAtZero() {
		qt()
			.forAll(
				simSetups())
			.check(
				setup -> genState(setup).currentTime() == 0);
	}

	SimState genState(SimSetup setup) {
		return SimStateGenerator.create(setup).generateState();
	}
}
