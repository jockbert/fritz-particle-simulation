package com.kastrull.fritz.primitives;

import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;

import com.kastrull.fritz.Laws;

public interface WithQtAndPrimitives extends WithQuickTheories {

	default Gen<Double> doublesWithInf() {
		return doubles().any();
	}

	default Gen<Coord> coords() {
		return doublesWithInf().zip(doublesWithInf(), Coord::c);
	}

	default Gen<Particle> particles() {
		return coords().zip(coords(), Particle::p);
	}

	default Gen<Double> degrees() {
		// A little bit over a complete rotation (7 > 2*pi) in both positive
		// and negative direction.
		return doubles().between(-7, 7);
	}

	default Gen<Double> boxedDoubles() {
		return doubles().between(-Laws.MAX_SIZE, Laws.MAX_SIZE);
	}

	default Gen<Coord> boxedCoords() {
		return boxedDoubles().zip(boxedDoubles(), Coord::c);
	}

	default Gen<Particle> boxedParticles() {
		return boxedCoords().zip(boxedCoords(), Particle::p);
	}

	default Gen<Particle> boxedParticlesYPositive() {
		return boxedCoordsYPositive()
			.zip(boxedCoordsYPositive(), Particle::p);
	}

	default Gen<Coord> boxedCoordsYPositive() {
		return boxedDoublesYPositive()
			.zip(boxedDoublesYPositive(), Coord::c);
	}

	default Gen<Double> boxedDoublesYPositive() {
		return doubles().between(0, Laws.MAX_SIZE);
	}

	default Gen<Border> boxedBorders() {
		return booleans().all().zip(
			boxedDoubles(),
			(byX, at) -> Border.b(at, byX));
	}

}
