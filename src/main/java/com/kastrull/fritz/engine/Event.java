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
public interface Event<R> {
	double time();

	Action<R> action();

	default Set<Integer> involving() {
		return Collections.emptySet();
	}

	static <S> ImmutableEvent<S> of(double atTime, Action<S> action) {
		return ImmutableEvent.<S>of(atTime, action);
	}
}