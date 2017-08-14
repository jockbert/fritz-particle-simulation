package com.kastrull.fritz.sim.event;

public final class SimEndEvent extends Event {

	public SimEndEvent(double atTime) {
		super(atTime);
	}

	@Override
	public boolean apply() {
		return true;
	}
}