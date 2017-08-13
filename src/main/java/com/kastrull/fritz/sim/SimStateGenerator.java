package com.kastrull.fritz.sim;

import static com.codepoetics.protonpack.StreamUtils.zip;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Particle;

public final class SimStateGenerator {

	private static final double ZERO = 0.0;
	private static final double START_TIME = 0.0;

	public static SimStateGenerator create(SimSetup setup) {
		return new SimStateGenerator(setup);
	}

	public static SimState generate(SimSetup setup) {
		return new SimStateGenerator(setup).generateState();
	}

	private final SimSetup setup;
	private final Random r;

	public SimStateGenerator(SimSetup setup) {
		this.setup = setup;

		r = new Random(setup.rndSeed);
	}

	private Stream<Particle> particleStream() {
		Stream<Double> posX = setup.size.x == 0 ? Stream.generate(() -> 0.0) : r.doubles(ZERO, setup.size.x).boxed();
		Stream<Double> posY = setup.size.y == 0 ? Stream.generate(() -> 0.0) : r.doubles(ZERO, setup.size.y).boxed();
		Stream<Coord> pos = zip(posX, posY, Coord::c);

		Stream<Coord> vel;
		double max = setup.maxSpeed;
		if (max == 0.0) {
			vel = Stream.generate(() -> Coord.ZERO);
		} else {
			Stream<Double> velX = r.doubles(-max, max + Double.MIN_NORMAL).boxed();
			Stream<Double> velY = r.doubles(-max, max + Double.MIN_NORMAL).boxed();
			vel = zip(velX, velY, Coord::c);
		}
		return zip(pos, vel, Particle::p);
	}

	public SimState generateState() {
		return SimState
			.createWithWalls(setup.size.x, setup.size.y)
			.currentTime(START_TIME)
			.targetTime(setup.simTime)
			.particles(particleListOfLength(setup.particleCount));
	}

	private List<Particle> particleListOfLength(int length) {
		return particleStream().limit(length).collect(Collectors.toList());
	}
}
