package com.kastrull.fritz.sim.event;

import java.util.stream.Stream;

public final class SimEndEvent extends Event {

	public SimEndEvent(double atTime) {
		super(atTime);
	}

	@Override
	public boolean apply() {
		return true;
	}

	@Override
	public Stream<Event> next() {
		return Stream.empty();
	}
}