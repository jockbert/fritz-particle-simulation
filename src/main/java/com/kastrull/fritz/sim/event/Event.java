package com.kastrull.fritz.sim.event;

import java.util.stream.Stream;

abstract public class Event implements Comparable<Event> {

	private final double atTime;

	protected Event(double atTime) {
		this.atTime = atTime;
	}

	public final double atTime() {
		return atTime;
	}

	/** @return true if end time is reached, otherwise false. */
	public abstract boolean apply();

	/**
	 * Note: this class has a natural ordering that is inconsistent with equals.
	 */
	@Override
	public int compareTo(Event o) { // NOSONAR
		int result = 0;
		if (atTime < o.atTime())
			result = -1;
		if (atTime > o.atTime())
			result = 1;

		return result == 0
				? simEndEventIsLastAmongSameTimes(o)
				: result;
	}

	private int simEndEventIsLastAmongSameTimes(Event o) {
		if (this instanceof SimEndEvent) {
			return 1;
		} else if (o instanceof SimEndEvent) {
			return -1;
		}
		return 0;
	}

	public abstract Stream<Integer> getPids();
}
