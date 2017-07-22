package com.kastrull.fritz.primitives;

import static com.kastrull.fritz.primitives.Coord.c;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CoordTest implements WithQtAndPrimitives {

	@Test
	public void canCreate() {
		qt()
			.forAll(
				doublesToInf(),
				doublesToInf())
			.check((x, y) -> {
				Coord c = Coord.c(x, y);
				return c != null && c.x == x && c.y == y;
			});
	}

	@Test
	public void canAdd() {
		qt()
			.forAll(
				coords(),
				coords())
			.checkAssert((a, b) -> {
				Coord actual = a.add(b);
				Coord expected = Coord.c(a.x + b.x, a.y + b.y);
				assertEquals(expected, actual);
			});
	}

	@Test
	public void canMult() {
		qt()
			.forAll(
				coords(),
				doublesToInf())
			.checkAssert((c, z) -> {
				Coord actual = c.mult(z);
				Coord expected = Coord.c(c.x * z, c.y * z);
				assertEquals(actual, expected);
			});
	}

	@Test
	public void subtractSelf() {
		qt().forAll(coords())
			.assuming(c -> c.isFinite())
			.check(c -> c.subtract(c).equals(Coord.ZERO));
	}

	@Test
	public void subtractZero() {
		qt().forAll(coords())
			.check(c -> c.subtract(Coord.ZERO).equals(c));
	}

	@Test
	public void subtractUnit() {
		qt().forAll(coords())
			.check(c -> c.subtract(Coord.UNIT).equals(Coord.c(c.x - 1, c.y - 1)));
	}

	@Test
	public void absSqr() {
		assertEquals(0, c(0, 0).absSqr(), 0);
		assertEquals(5, c(-1, -2).absSqr(), 0);
		assertEquals(25, c(4, 3).absSqr(), 0);
	}
}
