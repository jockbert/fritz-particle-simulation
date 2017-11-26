package com.kastrull.fritz.sim;

import static com.kastrull.fritz.primitives.Border.BY_X;
import static com.kastrull.fritz.primitives.Border.BY_Y;
import static com.kastrull.fritz.primitives.Border.b;
import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kastrull.fritz.physics.LinearPhysics;
import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.WithAssert;

public class BasicSimulatorTest implements WithAssert {

	private Simulator cut = new BasicSimulator(new LinearPhysics());

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
			.particles(p(c(8, 5), c(-1, 0)));

		assertEndState(expected, start);
	}

	@Test
	public void testTwoWallHit() {
		SimState start = stateTenByTenBox()
			.addParticle(p(c(5, 5), c(0, 1)))
			.targetTime(13);

		SimState expectedEnd = start
			.currentTime(13)
			.particles(p(c(5, 2), c(0, 1)));

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
			.particles(p(startPos, startVelocity.negate()));

		assertEndState(expectedEnd, start);

	}

	private void assertEndState(SimState expectedEndState, SimState startState) {
		SimState endState = cut.simulate(startState);
		assertEquals(expectedEndState, endState);
	}

	private SimState stateTenByTenBox() {
		return SimState.NULL
			.addWall(b(0, BY_X))
			.addWall(b(0, BY_Y))
			.addWall(b(10, BY_X))
			.addWall(b(10, BY_Y));
	}
}
