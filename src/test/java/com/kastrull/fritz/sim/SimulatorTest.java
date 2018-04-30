package com.kastrull.fritz.sim;

import static com.kastrull.fritz.primitives.Border.BY_X;
import static com.kastrull.fritz.primitives.Border.BY_Y;
import static com.kastrull.fritz.primitives.Border.b;
import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static com.kastrull.fritz.sim.SimState.mut;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kastrull.fritz.primitives.Coord;

public abstract class SimulatorTest {

	protected abstract Simulator cut();

	// TODO test for wall collision before sim end.
	// TODO take care of executing particle collisions before exiting

	@Test
	public void testEmpty() {
		SimState start = stateTenByTenBox()
			.targetTime(10);

		SimState expected = mut(start)
			.currentTime(10);

		assertEndState(expected, start);
	}

	@Test
	public void testOneWallHit() {
		SimState start = stateTenByTenBox()
			.particles(p(c(5, 5), c(1, 0)))
			.targetTime(5);

		SimState expected = mut(start)
			.currentTime(5)
			.particles(p(c(8, 5), c(-1, 0)))
			.wallAbsorbedMomentum(2);

		assertEndState(expected, start);
	}

	@Test
	public void testTwoWallHit() {
		SimState start = stateTenByTenBox()
			.particles(p(c(5, 5), c(0, 1)))
			.targetTime(13);

		SimState expectedEnd = mut(start)
			.currentTime(13)
			.particles(p(c(5, 2), c(0, 1)))
			.wallAbsorbedMomentum(4);

		assertEndState(expectedEnd, start);
	}

	@Test
	public void testCornerCollisionReflex() {

		// Will reach collision point (1,1) in 2 time units, and 2 more time
		// units to reach the start point again, but with negated velocity.

		Coord startPos = c(3, 5);
		Coord startVelocity = c(-1, -2);

		SimState start = stateTenByTenBox()
			.particles(p(startPos, startVelocity))
			.targetTime(4);

		SimState expectedEnd = mut(start)
			.currentTime(4)
			.particles(p(startPos, startVelocity.negate()))
			.wallAbsorbedMomentum(6);

		assertEndState(expectedEnd, start);
	}

	@Test
	public void testParticleCollision() {

		// 3_ 4_ 5_ 6_ 7_
		// a> .. .. b. ..
		// .. a. .. .. b>

		SimState start = stateTenByTenBox()
			.particles(
				p(c(3, 5), c(1, 0)),
				p(c(6, 5), c(0, 0)))
			.targetTime(2);

		SimState expected = stateTenByTenBox()
			.particles(
				p(c(4, 5), c(0, 0)),
				p(c(7, 5), c(1, 0)))
			.targetTime(2)
			.currentTime(2);

		assertEndState(expected, start);
	}

	@Test
	public void testPendulumWave() {

		// 2_ 3_ 4_ 5_ 6_ 7_ 8_ 9_
		// a> .. .. b. .. .. c. ..
		// .. a. .. .. b. .. .. c>

		SimState proto = stateBox(100, 100)
			.targetTime(3);

		Coord moving = c(1, 0);
		Coord stationary = c(0, 0);

		SimState start = mut(proto)
			.particles(
				p(c(2, 10), moving),
				p(c(5, 10), stationary),
				p(c(8, 10), stationary));

		SimState expected = mut(proto)
			.particles(
				p(c(3, 10), stationary),
				p(c(6, 10), stationary),
				p(c(9, 10), moving))
			.currentTime(3);

		assertEndState(expected, start);
	}

	@Test
	public void testTwoParticlesWithCornerCollision() {
		// Example 3 from particle_sim_config

		SimState proto = stateBox(6, 4)
			.targetTime(3);

		SimState start = mut(proto)
			.particles(
				p(c(1, 1), c(1, 1)),
				p(c(5, 3), c(-1, -1)));

		SimState expected = mut(proto)
			.particles(
				p(c(2, 2), c(-1, -1)),
				p(c(4, 2), c(1, 1)))
			.currentTime(3)
			.wallAbsorbedMomentum(8);

		assertEndState(expected, start);
	}

	private void assertEndState(SimState expectedEndState, SimState startState) {
		SimState endState = cut().simulate(startState);
		assertEquals(expectedEndState.toString(), endState.toString());
	}

	private ImmutableSimState stateTenByTenBox() {
		return SimState.of().walls(
			b(0, BY_X),
			b(0, BY_Y),
			b(10, BY_X),
			b(10, BY_Y));
	}

	private ImmutableSimState stateBox(int width, int height) {
		return SimState.of().walls(
			b(0, BY_X),
			b(0, BY_Y),
			b(width, BY_X),
			b(height, BY_Y));
	}
}