package com.kastrull.fritz.primitives;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CoordTest implements WithQtAndPrimitives {

	@Test
	public void canCreate() {
		qt()
			.forAll(
				doubleWithInf(),
				doubleWithInf())
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
				doubleWithInf())
			.checkAssert((c, z) -> {
				Coord actual = c.mult(z);
				Coord expected = Coord.c(c.x * z, c.y * z);
				assertEquals(actual, expected);
			});
	}
}
