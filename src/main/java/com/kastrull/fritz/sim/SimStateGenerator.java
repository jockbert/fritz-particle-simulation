package com.kastrull.fritz.sim;

import java.util.ArrayList;
import java.util.List;
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
		return SimState
			.createWithWalls(setup.size.x, setup.size.y)
			.currentTime(START_TIME)
			.targetTime(setup.simTime)
			.particles(randomParticles(setup.particleCount));
	}

	private final Particle randomParticle() {
		return Particle.p(randomCoord(), randomCoord());
	}

	private final Coord randomCoord() {
		return Coord.c(r.nextDouble(), r.nextDouble());
	}

	private List<Particle> randomParticles(int count) {
		List<Particle> ps = new ArrayList<>(count);

		for (int i = 0; i < count; i++)
			ps.add(randomParticle());

		return ps;
	}
}
