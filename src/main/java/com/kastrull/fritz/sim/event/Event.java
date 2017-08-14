package com.kastrull.fritz.sim.event;

abstract public class Event implements Comparable<Event> {

	private final double atTime;

	Event(double atTime) {
		this.atTime = atTime;
	}

	final double atTime() {
		return atTime;
	}

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
		return result;
	}

}
