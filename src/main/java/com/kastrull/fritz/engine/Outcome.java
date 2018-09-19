package com.kastrull.fritz.engine;

import java.util.Collections;
import java.util.Set;

import org.immutables.value.Value;

/** The outcome of an executed {@link Event} in {@link EventEngine}. */
@Value.Immutable(builder = false)
@Value.Style(allMandatoryParameters = true, defaultAsDefault = true, with = "")
public interface Outcome<I, R> {

	double time();

	R result();

	default Set<I> involves() {
		return Collections.emptySet();
	}

	default Set<I> invalidates() {
		return Collections.emptySet();
	}

	/** Creation convenience. */
	static <I2, R2> ImmutableOutcome<I2, R2> of(double atTime, R2 value) {
		return ImmutableOutcome.<I2, R2>of(atTime, value);
	}
}
