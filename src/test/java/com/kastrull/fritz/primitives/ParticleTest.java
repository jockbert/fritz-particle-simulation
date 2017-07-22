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
}
