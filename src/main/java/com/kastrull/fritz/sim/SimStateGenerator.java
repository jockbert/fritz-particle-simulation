package com.kastrull.fritz.sim;

import java.util.Random;

import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Particle;

public final class SimStateGenerator {

	private static final double START_TIME = 0.0;

	public static SimStateGenerator create(SimSetup setup) {
		return new SimStateGenerator(setup);
	}

	private final SimSetup setup;
	private final Random r;

	public SimStateGenerator(SimSetup setup) {
		this.setup = setup;
		this.r = new Random(setup.rndSeed);
	}

	public SimState generateState() {

		Particle randomParticle = Particle.p(Coord.c(r.nextDouble(), r.nextDouble()),
			Coord.c(r.nextDouble(), r.nextDouble()));

		return SimState
			.createWithWalls(setup.size.x, setup.size.y)
			.currentTime(START_TIME)
			.targetTime(setup.simTime)
			.addParticle(randomParticle);
	}
}
