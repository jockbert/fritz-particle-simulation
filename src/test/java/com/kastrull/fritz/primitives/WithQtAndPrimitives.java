package com.kastrull.fritz.primitives;

import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.core.Source;

import com.kastrull.fritz.Laws;

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

	default Source<Double> boxedDoubles() {
		return doubles().fromZeroToOne().as(
			d -> (d - 0.5) * 2 * Laws.MAX_SIZE,
			d -> d / (2 * Laws.MAX_SIZE) + 0.5);
	}

	default Source<Coord> boxedCoords() {
		return Source.of(
			boxedDoubles().combine(boxedDoubles(), Coord::c));
	}

	default Source<Particle> boxedParticles() {
		return Source.of(
			boxedCoords().combine(boxedCoords(),
				Particle::p));
	}

	default Source<Border> boxedBorders() {
		return Source.of(
			booleans().all().combine(boxedDoubles(),
				(byX, at) -> Border.b(at, byX)));
	}

}
