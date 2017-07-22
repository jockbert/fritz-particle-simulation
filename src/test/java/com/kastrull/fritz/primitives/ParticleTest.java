package com.kastrull.fritz.primitives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ParticleTest implements WithQtAndPrimitives {

	@Test
	public void canCreate() throws Exception {
		qt()
			.forAll(
				coords(),
				coords())
			.checkAssert((position, velocity) -> {
				Particle p = Particle.p(position, velocity);
				assertNotNull(p);
				assertEquals(p.pos, position);
				assertEquals(p.vel, velocity);
			});
	}

	@Test
	public void subtractSelf() {
		qt().forAll(particles())
			.assuming(p -> p.isFinite())
			.check(p -> p.subtract(p).equals(Particle.ZERO));
	}

	@Test
	public void subtractZero() {
		qt().forAll(particles())
			.check(p -> p.subtract(Particle.ZERO).equals(p));
	}

	@Test
	public void subtractUnit() {
		qt().forAll(particles())
			.check(p -> p.subtract(Particle.UNIT)
				.equals(Particle.p(
					p.pos.subtract(Coord.UNIT),
					p.vel.subtract(Coord.UNIT))));
	}

}
