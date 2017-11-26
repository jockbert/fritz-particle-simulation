package com.kastrull.fritz.sim.event;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;

public class EventTest {

	private static final double SAME_TIME = 1.234;

	@Test
	public void testSimEndEventIsOrderedAfterAllOtherEventsUsingSameTime() {

		Event simEnd = new SimEndEvent(SAME_TIME);
		Event otherEvent = dummyEvent(SAME_TIME);

		assertOrder(otherEvent, simEnd);
	}

	@Test
	public void testEarlerSimEventIsSmaller() {
		Event simEnd = new SimEndEvent(SAME_TIME);
		Event otherEvent = dummyEvent(SAME_TIME + 0.01);

		assertOrder(simEnd, otherEvent);
	}

	private void assertOrder(Event smallest, Event largest) {
		assertEquals("smallest first",
			-1, smallest.compareTo(largest));

		assertEquals("largest first",
			1, largest.compareTo(smallest));
	}

	private Event dummyEvent(double atTime) {
		return new Event(atTime) {

			@Override
			public boolean apply() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Stream<Integer> getPids() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
