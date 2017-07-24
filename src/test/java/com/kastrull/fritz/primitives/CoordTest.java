package com.kastrull.fritz.primitives;

import static com.kastrull.fritz.primitives.Coord.c;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CoordTest implements WithQtAndPrimitives, WithAssert {

	@Test
	public void create() {
		qt()
			.forAll(
				doublesWithInf(),
				doublesWithInf())
			.check((x, y) -> {
				Coord c = c(x, y);
				return c != null && c.x == x && c.y == y;
			});
	}

	@Test
	public void add() {
		qt()
			.forAll(
				coords(),
				coords())
			.checkAssert((a, b) -> {
				Coord actual = a.add(b);
				Coord expected = c(a.x + b.x, a.y + b.y);
				assertEquals(expected, actual);
			});
	}

	@Test
	public void mult() {
		qt()
			.forAll(
				coords(),
				doublesWithInf())
			.checkAssert((c, z) -> {
				Coord actual = c.mult(z);
				Coord expected = c(c.x * z, c.y * z);
				assertEquals(actual, expected);
			});
	}

	@Test
	public void subtract_self() {
		qt().forAll(coords())
			.assuming(c -> c.isFinite())
			.check(c -> c.subtract(c).equals(Coord.ZERO));
	}

	@Test
	public void subtract_zero() {
		qt().forAll(coords())
			.check(c -> c.subtract(Coord.ZERO).equals(c));
	}

	@Test
	public void subtract_unit() {
		qt().forAll(coords())
			.check(c -> c.subtract(Coord.UNIT).equals(Coord.c(c.x - 1, c.y - 1)));
	}

	@Test
	public void distanceAbsSqr() {
		assertExact(0, c(0, 0).distansSqr());
		assertExact(5, c(-1, -2).distansSqr());
		assertExact(25, c(4, 3).distansSqr());
	}

	@Test
	public void yConjugate() {
		qt().forAll(coords()).check(c -> c.x == c.yConjugate().x && c.y == -c.yConjugate().y);
	}

	@Test
	public void distance() {
		assertExact(0.0, c(0, 0).distance());
		assertExact(1.0, c(-1, 0).distance());
		assertExact(2.0, c(0, 2).distance());
		assertExact(5.0, c(3, -4).distance());
	}

	@Test
	public void rotate_examples() {
		assertApprox(c(0, 0), c(0, 0).rotate(123456));
		assertApprox(c(1, 0), c(1, 0).rotate(Math.PI * 0 / 2));
		assertApprox(c(0, 1), c(1, 0).rotate(Math.PI * 1 / 2));
		assertApprox(c(-1, 0), c(1, 0).rotate(Math.PI * 2 / 2));
		assertApprox(c(0, -1), c(1, 0).rotate(Math.PI * 3 / 2));
		assertApprox(c(1, 0), c(1, 0).rotate(Math.PI * 4 / 2));

		assertApprox(c(-3, -2), c(-2, 3).rotate(Math.PI * 1 / 2));
	}

	@Test
	public void rotate_absIsUnchanged() {
		qt()
			.forAll(coords(), degrees())
			// arithmetic problems for really small values
			.assuming((c, deg) -> c.distance() > 1e-150)
			.assuming((c, deg) -> c.isFinite())
			.checkAssert(
				(c, deg) -> assertEquals(
					c.distance(),
					c.rotate(deg).distance(),
					c.distance() * Coord.EPSILON));
	}

	@Test
	public void approxEq() {
		double lessThanApprox = Coord.EPSILON / 10;
		double moreThanApprox = Coord.EPSILON * 10;
		assertApprox(c(0, 0), c(lessThanApprox, lessThanApprox));
		assertNotApprox(c(0, 0), c(moreThanApprox, moreThanApprox));
	}
}
