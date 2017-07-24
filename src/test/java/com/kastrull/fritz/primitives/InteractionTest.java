package com.kastrull.fritz.primitives;

import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Interaction.i;
import static com.kastrull.fritz.primitives.Particle.p;

import org.junit.Test;

public class InteractionTest implements WithQtAndPrimitives, WithAssert {

	@Test
	public void approxEquals() {

		double smallDiff = Coord.EPSILON / 10;
		double largeDiff = Coord.EPSILON * 10;

		Particle p0 = p(c(0, 0), c(0, 0));
		Particle ps = p(c(smallDiff, smallDiff), c(smallDiff, smallDiff));
		Particle pl = p(c(0, 0), c(0, largeDiff));

		Interaction i0 = i(p0, p0);

		assertApprox(i0, i(ps, ps));
		assertNotApprox(i0, i(p0, pl));
		assertNotApprox(i0, i(pl, p0));
	}
}
