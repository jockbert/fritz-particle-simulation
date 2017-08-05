package com.kastrull.fritz.sim;

import static org.quicktheories.quicktheories.generators.SourceDSL.doubles;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;

import org.javatuples.Quartet;
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
		// TODO needs rework
		return doubles().fromZeroToOne().as(
			d -> d * Laws.MAX_SPEED,
			d -> d / Laws.MAX_SPEED);
	}

	default Source<Double> simTimes() {
		return doubles().fromZeroToOne().as(
			d -> d * Laws.MAX_SIM_TIME,
			d -> d / Laws.MAX_SIM_TIME);
	}

	default Source<String> names() {
		return strings().allPossible().ofLengthBetween(0, 50);
	}

	default Source<SimSetup> simSetups() {
		return Source.of(boxedSizes()
			.combine(
				particleCounts(),
				speeds(),
				rndSeeds(),
				Quartet::with)
			.combine(
				simTimes(),
				names(),
				(q, simTime, name) -> {
					Coord size = q.getValue0();
					Integer particleCount = q.getValue1();
					Double maxSpeed = q.getValue2();
					Double rndSeed = q.getValue3();
					return SimSetup.ss(
						size,
						particleCount,
						maxSpeed,
						rndSeed,
						simTime,
						name);
				}));
	}
}
