package com.kastrull.fritz.engine;

import java.util.Collections;
import java.util.Set;

import org.immutables.value.Value;

@Value.Immutable(builder = false)
@Value.Style(allMandatoryParameters = true, defaultAsDefault = true, with = "")
public interface Outcome<R> {

	double time();

	R result();

	default Set<Integer> involves() {
		return Collections.emptySet();
	}

	default Set<Integer> invalidates() {
		return Collections.emptySet();
	}

	static <S> ImmutableOutcome<S> of(double atTime, S value) {
		return ImmutableOutcome.<S>of(atTime, value);
	}
}
