package com.kastrull.fritz.sim;

import java.util.Collections;
import java.util.List;

import org.immutables.value.Value;

import com.kastrull.fritz.engine.Event;
import com.kastrull.fritz.engine.EventEngine;
import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Particle;

/** The outcome of an executed {@link Event} in {@link EventEngine}. */
@Value.Immutable(builder = false)
@Value.Style(allMandatoryParameters = true, defaultAsDefault = true, with = "")
public interface SimState {

	/** Creation convenience. */
	static ImmutableSimState of() {
		return ImmutableSimState.of();
	}

	public static ImmutableSimState createWithWalls(double width, double height) {
		return of().walls(
			Border.b(0, Border.BY_X),
			Border.b(width, Border.BY_X),
			Border.b(0, Border.BY_Y),
			Border.b(height, Border.BY_Y));
	}

	/**
	 * Downcast to enable mutation or copy data into new
	 * {@link ImmutableSimState}.
	 */
	static ImmutableSimState mut(SimState ss) {
		if (ss instanceof ImmutableSimState)
			return (ImmutableSimState) ss;
		else
			return of()
				.currentTime(ss.currentTime())
				.targetTime(ss.targetTime())
				.wallAbsorbedMomentum(ss.wallAbsorbedMomentum())
				.particles(ss.particles())
				.walls(ss.walls());
	}

	default List<Particle> particles() {
		return Collections.emptyList();
	}

	default List<Border> walls() {
		return Collections.emptyList();
	}

	default double wallAbsorbedMomentum() {
		return 0.0;
	}

	default double targetTime() {
		return 0.0;
	}

	default double currentTime() {
		return 0.0;
	}
}
