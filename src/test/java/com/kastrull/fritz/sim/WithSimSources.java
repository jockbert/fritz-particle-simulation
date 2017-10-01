package com.kastrull.fritz.sim;

import static org.quicktheories.generators.SourceDSL.doubles;
import static org.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.generators.SourceDSL.longs;
import static org.quicktheories.generators.SourceDSL.strings;

import org.javatuples.Triplet;
import org.quicktheories.core.Gen;

import com.kastrull.fritz.Laws;
import com.kastrull.fritz.primitives.Coord;

public interface WithSimSources {

	default Gen<Double> boxedSizeComp() {
		return doubles()
			.fromZeroToOne()
			.map(d -> d * Laws.MAX_SIZE);
	}

	default Gen<Coord> boxedSizes() {
		return boxedSizeComp()
			.zip(boxedSizeComp(), Coord::c);
	}

	default Gen<Integer> particleCounts() {
		return integers().between(0, Laws.MAX_PARTICLE_COUNT);
	}

	default Gen<Double> speeds() {
		return doubles()
			.fromZeroToOne()
			.map(d -> d * Laws.MAX_SPEED);
	}

	default Gen<Long> rndSeeds() {
		return longs().all();
	}

	default Gen<Double> simTimes() {
		return doubles().fromZeroToOne()
			.map(d -> d * Laws.MAX_SIM_TIME);
	}

	default Gen<String> names() {
		return strings().allPossible().ofLengthBetween(0, 50);
	}

	default Gen<SimSetup> simSetups() {
		return boxedSizes()
			.zip(
				particleCounts(),
				speeds(),
				Triplet::with)
			.zip(
				simTimes(),
				rndSeeds(),
				(q, simTime, rndSeed) -> {
					Coord size = q.getValue0();
					Integer particleCount = q.getValue1();
					Double maxSpeed = q.getValue2();
					return SimSetup.ss(
						size,
						particleCount,
						maxSpeed,
						simTime,
						rndSeed,
						"SomeName");
				});
	}
}
