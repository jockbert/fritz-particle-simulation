package com.kastrull.fritz.primitives;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.core.Source;

public class CoordTest implements WithQuickTheories {

	@Test
	public void coordCanBeCreated() {
		qt().forAll(doubleWithInf(), doubleWithInf())
			.check((x, y) -> {
				Coord c = Coord.c(x, y);
				return c != null && c.x == x && c.y == y;
			});
	}

	@Test
	public void canAdd() {
		qt().forAll(coords(), coords())
			.checkAssert((a, b) -> {
				Coord actual = a.add(b);
				Coord expected = Coord.c(a.x + b.x, a.y + b.y);
				assertEquals(expected, actual);
			});
	}

	@Test
	public void canMult() {
		qt().forAll(coords(), doubleWithInf())
			.checkAssert((a, z) -> {
				Coord actual = a.mult(z);
				Coord expected = Coord.c(a.x * z, a.y * z);
				assertEquals(actual, expected);
			});
	}

	private Source<Double> doubleWithInf() {
		return doubles().fromNegativeInfinityToPositiveInfinity();
	}

	private Source<Coord> coords() {
		return Source.of(
			doubleWithInf().combine(doubleWithInf(), Coord::c));
	}
}
