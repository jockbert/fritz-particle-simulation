package com.kastrull.fritz.engine;

import java.util.Collections;
import java.util.Set;

import org.immutables.value.Value;

/**
 * An event contains of three things - a time, an action (i.e. an delayed
 * calculation and result) and a set of related items. The action result can be
 * useful for distinguishing between different types of events for items.
 */
@Value.Immutable(builder = false)
@Value.Style(allMandatoryParameters = true, defaultAsDefault = true, with = "")
public interface Event<I, R> {
	double time();

	R result();

	default Set<I> involving() {
		return Collections.emptySet();
	}

	/** Creation convenience. */
	static <I2, R2> ImmutableEvent<I2, R2> of(double atTime, R2 result) {
		return ImmutableEvent.<I2, R2>of(atTime, result);
	}
}