package com.kastrull.fritz.sim;

import static com.kastrull.fritz.primitives.Border.BY_X;
import static com.kastrull.fritz.primitives.Border.BY_Y;
import static com.kastrull.fritz.primitives.Border.b;
import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.kastrull.fritz.physics.LinearPhysics;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.primitives.WithAssert;

public class SimulatorTest implements WithAssert {

	private static List<Particle> EMPTY = new ArrayList<>();

	private Simulator cut = new BasicSimulator(new LinearPhysics());

	@Test
	public void testEmpty() {

		SimState startState = stateTenByTenBox().targetTime(10);

		SimState endState = cut.simulate(startState);

		assertExact("CurrentTime", 10, endState.currentTime());
		assertEquals(startState, endState.currentTime(0));
	}

	@Test
	public void testOneWallHit() {

		SimState startState = stateTenByTenBox()
			.addParticle(p(c(5, 5), c(1, 0)))
			.targetTime(6);

		SimState endState = cut.simulate(startState);

		SimState expectedEndState = startState
			.currentTime(6)
			.particles(p(c(9, 5), c(-1, 0)));

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
