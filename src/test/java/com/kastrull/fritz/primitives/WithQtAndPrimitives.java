package com.kastrull.fritz.primitives;

import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.core.Source;

public interface WithQtAndPrimitives extends WithQuickTheories {

	default Source<Double> doublesToInf() {
		return doubles().fromNegativeInfinityToPositiveInfinity();
	}

	default Source<Coord> coords() {
		return Source.of(
			doublesToInf().combine(doublesToInf(), Coord::c));
	}

	default Source<Particle> particles() {
		return Source.of(
			coords().combine(coords(), Particle::p));
	}

}
