package com.kastrull.fritz.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void targetTime() {
		qt()
			.forAll(
				simSetups())
			.check(
				setup -> genState(setup).targetTime() == setup.simTime);
	}

	@Test
	public void walls() {
		qt()
			.forAll(
				simSetups())
			.checkAssert(
				setup -> {
					List<Border> ws = genState(setup).walls();
					assertEquals(4, ws.size());

					Border west = Border.b(0, Border.BY_X);
					Border south = Border.b(0, Border.BY_Y);
					Border east = Border.b(setup.size.x, Border.BY_X);
					Border north = Border.b(setup.size.y, Border.BY_Y);

					assertContains(ws, west, "west");
					assertContains(ws, east, "east");
					assertContains(ws, south, "south");
					assertContains(ws, north, "north");
				});
	}

	@Test
	public void sameSetupGeneratesSameStatesInDifferentGenerators() {
		qt()
			.forAll(
				simSetups())
			.checkAssert(
				setup -> {
					SimStateGenerator g1 = SimStateGenerator.create(setup);
					SimStateGenerator g2 = SimStateGenerator.create(setup);
					assertEquals(g1.generateState(), g2.generateState());
					assertEquals(g1.generateState(), g2.generateState());
					assertEquals(g1.generateState(), g2.generateState());
					assertEquals(g1.generateState(), g2.generateState());
				});
	}

	private <T> void assertContains(List<T> ts, T t, String mnemonic) {
		assertTrue(ts + " contains " + mnemonic + " " + t, ts.contains(t));
	}

	SimState genState(SimSetup setup) {
		return SimStateGenerator.create(setup).generateState();
	}
}
