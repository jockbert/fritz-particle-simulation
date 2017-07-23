package com.kastrull.fritz.primitives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class BorderTest implements WithQtAndPrimitives {

	@Test
	public void canCreate() {
		qt()
			.forAll(
				doublesWithInf(),
				booleans().all())
			.checkAssert((at, byX) -> {
				Border b = Border.b(at, byX);

				assertNotNull(b);
				assertEquals(at, b.at, 0.0);
				assertEquals(byX, b.byX);
			});
	}
}
