package com.kastrull.fritz.primitives;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class CoordGen extends Generator<Coord> {

	static int SPREAD = 10;

	public CoordGen() {
		super(Coord.class);
	}

	@Override
	public Coord generate(SourceOfRandomness r, GenerationStatus status) {
		double x = r.nextGaussian() * SPREAD;
		double y = r.nextGaussian() * SPREAD;
		return Coord.c(x, y);
	}
}