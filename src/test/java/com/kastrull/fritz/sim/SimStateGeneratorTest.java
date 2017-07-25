package com.kastrull.fritz.sim;

import java.util.stream.Stream;

import org.junit.Test;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Particle;

public class SimStateGeneratorTest {
	SimStateGenerator generator = SimStateGenerator.create();

	@Test
	public void use() {
		SimState state = generator.generateSimState(SimSetup.NULL);

		Stream<Border> walls = state.walls();
		Stream<Particle> particles = state.particles();

		double currentTime = state.currentTime();
		double targetTime = state.targetTime();
		double wallAbsorbedMomentum = state.wallAbsorbedMomentum();
	}
}
