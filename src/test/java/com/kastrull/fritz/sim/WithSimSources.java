package com.kastrull.fritz.sim;

import static org.quicktheories.quicktheories.generators.SourceDSL.doubles;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;

import org.quicktheories.quicktheories.core.Source;

import com.kastrull.fritz.Laws;
import com.kastrull.fritz.primitives.Coord;

public interface WithSimSources {

	default Source<Double> boxedSizeComp() {
		return Source
			.of(doubles().fromZeroToOne().as(
				d -> d * Laws.MAX_SIZE,
				d -> d / Laws.MAX_SIZE));
	}

	default Source<Coord> boxedSizes() {
		return Source.of(boxedSizeComp()
			.combine(boxedSizeComp(), Coord::c));
	}

	default Source<Integer> particleCounts() {
		return integers().allPositive();

	}

	default Source<Double> speeds() {
		return doubles().fromZeroToOne().as(
			d -> d * Laws.MAX_SPEED,
			d -> d / Laws.MAX_SPEED);
	}

	default Source<Double> rndSeeds() {
		return doubles().fromZeroToOne().as(
			d -> d * Laws.MAX_SPEED,
			d -> d / Laws.MAX_SPEED);
	}

	default Source<String> names() {
		return strings().allPossible().ofLengthBetween(0, 50);
	}
}
