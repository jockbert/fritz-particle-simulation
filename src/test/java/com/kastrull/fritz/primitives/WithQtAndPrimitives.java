package com.kastrull.fritz.primitives;

import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.core.Source;

public interface WithQtAndPrimitives extends WithQuickTheories {

	default Source<Double> doubleWithInf() {
		return doubles().fromNegativeInfinityToPositiveInfinity();
	}

	default Source<Coord> coords() {
		return Source.of(
			doubleWithInf().combine(doubleWithInf(), Coord::c));
	}

}
