package com.kastrull.fritz.primitives;

import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Interaction.i;
import static com.kastrull.fritz.primitives.Particle.p;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InteractionTest implements WithQtAndPrimitives {

	@Test
	public void approxEquals() {

		double smallDiff = Coord.EPSILON / 10;
		double largeDiff = Coord.EPSILON * 10;

		Particle p0 = p(c(0, 0), c(0, 0));
		Particle ps = p(c(smallDiff, smallDiff), c(smallDiff, smallDiff));
		Particle pl = p(c(0, 0), c(0, largeDiff));

		Interaction i0 = i(p0, p0);

		assertTrue(i0.approxEq(i(ps, ps)));
		assertFalse(i0.approxEq(i(p0, pl)));
		assertFalse(i0.approxEq(i(pl, p0)));
	}
}
