package com.kastrull.fritz.primitives;

import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.core.Source;

public interface WithQtAndPrimitives extends WithQuickTheories {

	default Source<Double> doublesWithInf() {
		return doubles().fromNegativeInfinityToPositiveInfinity();
	}

	default Source<Coord> coords() {
		return Source.of(
			doublesWithInf().combine(doublesWithInf(), Coord::c));
	}

	default Source<Particle> particles() {
		return Source.of(
			coords().combine(coords(), Particle::p));
	}

	default Source<Double> degrees() {
		// A little bit over a complete rotation (4 radians) in both positive
		// and negative direction.
		return Source.of(doubles().fromZeroToOne()).as(
			d -> d * 8 - 4,
			e -> (e + 4) / 8);
	}

	default Source<Coord> coordsYPositive() {
		return Source.of(doublesWithInf()
			.combine(doubles().fromZeroToPositiveInfinity(),
				Coord::c));
	}

	default Source<Particle> particlesYPositive() {
		return Source.of(coordsYPositive()
			.combine(coordsYPositive(),
				(pos, vel) -> Particle.p(pos, vel)));

	}

}
