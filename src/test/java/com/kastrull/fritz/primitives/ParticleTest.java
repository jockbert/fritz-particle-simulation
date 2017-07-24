package com.kastrull.fritz.primitives;

import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ParticleTest implements WithQtAndPrimitives, WithAssert {

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

	@Test
	public void yConjugate() {
		qt()
			.forAll(particles())
			.check(p -> p.yConjugate().pos.equals(p.pos.yConjugate())
					&& p.yConjugate().vel.equals(p.vel.yConjugate()));
	}

	@Test
	public void rotate_examples() {
		assertApprox(
			p(c(0, 0), c(0, 0)),
			p(c(0, 0), c(0, 0)).rotate(123456));

		assertApprox(
			p(c(-4, 1), c(2, -3)),
			p(c(1, 4), c(-3, -2)).rotate(Math.PI / 2));
	}

	@Test
	public void rotate_distanceIsUnchanged() {
		qt()
			.forAll(particles(), degrees())
			// arithmetic problems for really small values
			.assuming((p, deg) -> p.distance() > 1e-150)
			.assuming((p, deg) -> p.isFinite())
			.checkAssert(
				(p, deg) -> assertEquals(
					p.distance(),
					p.rotate(deg).distance(),
					p.distance() * Particle.EPSILON));
	}

	@Test
	public void approxEq() {
		double lessThanApprox = Particle.EPSILON / 10;
		double moreThanApprox = Particle.EPSILON * 10;

		Particle zero = Particle.ZERO;
		assertApprox(zero, p(c(lessThanApprox, lessThanApprox), c(lessThanApprox, lessThanApprox)));

		assertNotApprox(zero, p(c(moreThanApprox, 0), c(0, 0)));
		assertNotApprox(zero, p(c(0, moreThanApprox), c(0, 0)));
		assertNotApprox(zero, p(c(0, 0), c(moreThanApprox, 0)));
		assertNotApprox(zero, p(c(0, 0), c(0, moreThanApprox)));
	}

	@Test
	public void distance() {
		assertExact(0.0, p(c(0, 0), c(0, 0)).distance());
		assertExact(1.0, p(c(1, 0), c(0, 0)).distance());
		assertExact(1.0, p(c(0, 1), c(0, 0)).distance());
		assertExact(1.0, p(c(0, 0), c(1, 0)).distance());
		assertExact(1.0, p(c(0, 0), c(0, 1)).distance());
		assertExact(5.0, p(c(0, -3), c(4, 0)).distance());
	}

}
