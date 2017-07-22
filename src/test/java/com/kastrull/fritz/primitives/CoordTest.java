package com.kastrull.fritz.primitives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;


@RunWith(JUnitQuickcheck.class)
public class CoordTest {

	@Property
	public void coordCanBeCreated(double x, double y) {
		Coord c = Coord.c(x, y);
		assertNotNull("Coord is not null", c);
		assertEquals(x, c.x, 0.0);
		assertEquals(y, c.y, 0.0);

	}
	
	@Property
	public void canAdd(@From(CoordGen.class) Coord a,@From(CoordGen.class) Coord b) {
		Coord actual = a.add(b);
		Coord expected = Coord.c(a.x + b.x, a.y + b.y);
		assertEquals(actual, expected);
	}

}
