package com.kastrull.fritz.sim;

import static com.kastrull.fritz.primitives.Border.BY_X;
import static com.kastrull.fritz.primitives.Border.BY_Y;
import static com.kastrull.fritz.primitives.Border.b;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kastrull.fritz.primitives.WithAssert;

public class SimulatorTest implements WithAssert {

	private Simulator cut = new BasicSimulator();

	@Test
	public void testEmpty() {

		SimState startState = stateTenByTenBox().targetTime(10);

		SimState endState = cut.simulate(startState);

		assertExact("CurrentTime", 10, endState.currentTime());
		assertEquals(startState, endState.currentTime(0));
	}

	private SimState stateTenByTenBox() {
		return SimState.NULL
			.addWall(b(0, BY_X))
			.addWall(b(0, BY_Y))
			.addWall(b(10, BY_X))
			.addWall(b(10, BY_Y));
	}
}
