package com.kastrull.fritz.sim;

import static com.kastrull.fritz.primitives.Border.BY_X;
import static com.kastrull.fritz.primitives.Border.BY_Y;
import static com.kastrull.fritz.primitives.Border.b;
import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kastrull.fritz.primitives.Coord;

public abstract class SimulatorTest {

	protected abstract Simulator cut();

	@Test
	public void testEmpty() {
		SimState start = stateTenByTenBox()
			.targetTime(10);

		SimState expected = start
			.currentTime(10);

		assertEndState(expected, start);
	}

	@Test
	public void testOneWallHit() {
		SimState start = stateTenByTenBox()
			.addParticle(p(c(5, 5), c(1, 0)))
			.targetTime(5);

		SimState expected = start
			.currentTime(5)
			.particles(p(c(8, 5), c(-1, 0)))
			.wallAbsorbedMomentum(2);

		assertEndState(expected, start);
	}

	@Test
	public void testTwoWallHit() {
		SimState start = stateTenByTenBox()
			.addParticle(p(c(5, 5), c(0, 1)))
			.targetTime(13);

		SimState expectedEnd = start
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
			.addParticle(p(startPos, startVelocity))
			.targetTime(4);

		SimState expectedEnd = start
			.currentTime(4)
			.particles(p(startPos, startVelocity.negate()))
			.wallAbsorbedMomentum(6);

		assertEndState(expectedEnd, start);
	}

	@Test
	public void testParticleCollision() {

		// 3 4 5 6 7
		// a> .. .. b. ..
		// .. a. .. .. b>

		SimState start = stateTenByTenBox()
			.addParticle(p(c(3, 5), c(1, 0)))
			.addParticle(p(c(6, 5), c(0, 0)))
			.targetTime(2);

		SimState expected = stateTenByTenBox()
			.addParticle(p(c(4, 5), c(0, 0)))
			.addParticle(p(c(7, 5), c(1, 0)))
			.targetTime(2)
			.currentTime(2);

		assertEndState(expected, start);
	}

	@Test
	public void testTwoParticlesWithCornerCollision() {
		// Example 3 from particle_sim_config

		// Bug to solve: Clear out old queued events related
		// particle when changed!!

		SimState proto = stateBox(6, 4)
			.targetTime(3);

		SimState start = proto
			.addParticle(p(c(1, 1), c(1, 1)))
			.addParticle(p(c(5, 3), c(-1, -1)));

		SimState expected = proto
			.addParticle(p(c(2, 2), c(-1, -1)))
			.addParticle(p(c(4, 2), c(1, 1)))
			.currentTime(3)
			.wallAbsorbedMomentum(8);

		assertEndState(expected, start);
	}

	private void assertEndState(SimState expectedEndState, SimState startState) {
		SimState endState = cut().simulate(startState);
		assertEquals(expectedEndState.toString(), endState.toString());
	}

	private SimState stateTenByTenBox() {
		return SimState.NULL
			.addWall(b(0, BY_X))
			.addWall(b(0, BY_Y))
			.addWall(b(10, BY_X))
			.addWall(b(10, BY_Y));
	}

	private SimState stateBox(int width, int height) {
		return SimState.NULL
			.addWall(b(0, BY_X))
			.addWall(b(0, BY_Y))
			.addWall(b(width, BY_X))
			.addWall(b(height, BY_Y));
	}

}