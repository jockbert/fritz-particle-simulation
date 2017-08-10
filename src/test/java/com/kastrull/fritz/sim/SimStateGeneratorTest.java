package com.kastrull.fritz.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.primitives.WithAssert;

public class SimStateGeneratorTest implements WithQuickTheories, WithSimSources, WithAssert {

	private static final double FIFTY_PERCENT = 0.30;

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
	public void momentumStartsAtZero() {
		qt()
			.forAll(
				simSetups())
			.check(
				setup -> genState(setup).wallAbsorbedMomentum() == 0);
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
	public void particleCount() {
		qt()
			.forAll(
				simSetups())
			.check(
				setup -> setup.particleCount == genState(setup).particles().size());
	}

	@Test
	public void particlePosition_avg() {
		qt()
			.forAll(simSetups())
			.assuming(setup -> setup.particleCount > 20)
			.checkAssert(
				setup -> {

					double expectedAvgPosX = setup.size.x / 2;
					double expectedAvgPosY = setup.size.y / 2;

					SimState state = genState(setup);

					double avgPosX = avg(state, p -> p.pos.x);
					double avgPosY = avg(state, p -> p.pos.y);

					assertEquals(expectedAvgPosX, avgPosX, expectedAvgPosX * FIFTY_PERCENT);
					assertEquals(expectedAvgPosY, avgPosY, expectedAvgPosY * FIFTY_PERCENT);
				});
	}

	@Test
	public void particleVelocity_avg() {
		qt()
			.forAll(
				simSetups())
			.assuming(setup -> setup.particleCount > 20)
			.checkAssert(
				setup -> {
					double expectedAvgSpeed = setup.maxSpeed / 2;

					SimState state = genState(setup);

					double avgSpeedX = avg(state, p -> Math.abs(p.vel.x));
					double avgSpeedY = avg(state, p -> Math.abs(p.vel.y));

					assertEquals(expectedAvgSpeed, avgSpeedX, expectedAvgSpeed * FIFTY_PERCENT);
					assertEquals(expectedAvgSpeed, avgSpeedY, expectedAvgSpeed * FIFTY_PERCENT);
				});
	}

	private double avg(SimState state, Function<Particle, Double> mapper) {

		double actualAccSpeedX = state
			.particles()
			.stream()
			.map(mapper)
			.reduce(0.0, (a, b) -> a + b);

		return actualAccSpeedX / state.particles().size();
	}

	@Test
	public void sameSetupGeneratesSameStatesInDifferentGenerators() {
		qt()
			.forAll(
				simSetups())
			.assuming(lowParticleCount())
			.checkAssert(
				setup -> {
					SimStateGenerator g1 = SimStateGenerator.create(setup);
					SimStateGenerator g2 = SimStateGenerator.create(setup);
					assertEquals(g1.generateState(), g2.generateState());
					assertEquals(g1.generateState(), g2.generateState());
					assertEquals(g1.generateState(), g2.generateState());
				});
	}

	private Predicate<SimSetup> lowParticleCount() {
		return setup -> setup.particleCount < 200;
	}

	@Test
	public void consecutiveStatesDiffer() {
		qt()
			.forAll(
				simSetups())
			.assuming(setup -> setup.particleCount > 0)
			.assuming(lowParticleCount())
			.checkAssert(
				setup -> {
					SimStateGenerator g = SimStateGenerator.create(setup);

					SimState s1 = g.generateState();
					SimState s2 = g.generateState();
					SimState s3 = g.generateState();
					SimState s4 = g.generateState();
					assertNotEquals(s1, s2);
					assertNotEquals(s2, s3);
					assertNotEquals(s3, s4);
				});
	}

	private <T> void assertContains(List<T> ts, T t, String mnemonic) {
		assertTrue(ts + " contains " + mnemonic + " " + t, ts.contains(t));
	}

	private SimState genState(SimSetup setup) {
		return SimStateGenerator.create(setup).generateState();
	}
}
