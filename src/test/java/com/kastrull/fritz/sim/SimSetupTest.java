package com.kastrull.fritz.sim;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

import com.kastrull.fritz.primitives.WithAssert;

public class SimSetupTest implements
		WithQuickTheories,
		WithSimSources,
		WithAssert {

	@Test
	public void create() {
		qt()
			.forAll(
				boxedSizes(),
				particleCounts(),
				speeds(),
				rndSeeds())
			.checkAssert((size, particleCount, maxSpeed, rndSeed) -> {
				String name = "asdfgtr";
				double simTime = 1234.03;

				SimSetup ss = SimSetup.ss(size, particleCount, maxSpeed, simTime, rndSeed, name);

				assertEquals(size, ss.size);
				assertExact(particleCount, ss.particleCount);
				assertExact(maxSpeed, ss.maxSpeed);
				assertExact(rndSeed, ss.rndSeed);
				assertExact(simTime, ss.simTime);
				assertEquals(name, ss.name);
			});
	}
}
