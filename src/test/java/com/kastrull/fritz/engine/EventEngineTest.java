package com.kastrull.fritz.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EventEngineTest {

	@Test
	public void testInterceptingEventsUsingHareAndTurtle() {

		EventEngine<String, String> engine = EventEngine.create();

		String hare = "Hare";
		String turtle = "Turtle";
		String winner = "Winner";

		engine.addEvent(
			12.3,
			"turtle wins race",
			turtle, winner);

		engine.addEvent(
			7.8,
			"hare intercepts turtle and wins race",
			hare, winner);

		assertNext(
			engine,
			Outcome.<String, String>of(
				7.8,
				"hare intercepts turtle and wins race")
				.involves(hare, winner)
				.invalidates(turtle));

		assertStop(engine);
	}

	@Test
	public void testUnrelatedEvents() {

		EventEngine<Integer, Boolean> engine = EventEngine.create();

		int eatApplePie = 0;
		int theEarthRevolves = 1;
		int throwADice = 2;

		engine.addEvent(1.1, true, eatApplePie);
		engine.addEvent(3.3, true, throwADice);
		engine.addEvent(2.2, false, theEarthRevolves);

		assertNext(engine, Outcome.<Integer, Boolean>of(1.1, true).involves(eatApplePie));
		assertNext(engine, Outcome.<Integer, Boolean>of(2.2, false).involves(theEarthRevolves));
		assertNext(engine, Outcome.<Integer, Boolean>of(3.3, true).involves(throwADice));

		assertStop(engine);
	}

	/**
	 * Description of some events:
	 * <ul>
	 * <li>E1 occurs at time 1 and involves item I1
	 * <li>E2 occurs at time 2 and involves item I2
	 * <li>E3 occurs at time 3 and involves item I3
	 * <li>E4 occurs at time 4 and involves item I1
	 * <li>E5 occurs at time 5 and involves items I1 and I3
	 * <li>E6 occurs at time 6 and involves no items
	 * <li>E7 occurs at time 7 and involves items 13 and I8
	 * <li>E8 occurs at time 8 and involves items I8
	 * </ul>
	 *
	 * The only events NOT invalidated by executing E1 are E2 and E6, since
	 * these events are not directly or indirectly related to item I1. Item I3
	 * is related to item I1 via event E5. For simplicity also event E3 is
	 * invalidated, despite occurring before E5. I8 is indirectly related to I1
	 * via events E5 and E7.
	 */
	@Test
	public void testIndirectlyRelatedEventsAreInvalidated() {

		EventEngine<Integer, Boolean> engine = EventEngine.create();

		int I1 = 1;
		int I2 = 2;
		int I3 = 3;
		int I8 = 8;

		engine.addEvent(1, true, I1);
		engine.addEvent(2, true, I2);
		engine.addEvent(3, true, I3);
		engine.addEvent(4, true, I1);
		engine.addEvent(5, true, I1, I3);
		engine.addEvent(6, true);
		engine.addEvent(7, true, I3, I8);
		engine.addEvent(8, true, I8);

		assertNext(engine, Outcome.<Integer, Boolean>of(1, true).involves(I1).invalidates(I3, I8));
		assertNext(engine, Outcome.<Integer, Boolean>of(2, true).involves(I2));
		assertNext(engine, Outcome.of(6, true));

		assertStop(engine);
	}

	private <I, R> void assertNext(EventEngine<I, R> engine, Outcome<I, R> expected) {
		assertTrue("engine has next", engine.hasNext());
		Outcome<I, R> actual = engine.next();

		assertEquals(expected.toString(), actual.toString());
		assertEquals("time for actual " + actual, expected.time(), actual.time(), Math.ulp(expected.time()));
		assertEquals("result for actual " + actual, expected.result(), actual.result());
		assertEquals("involves for actual " + actual, expected.involves(), actual.involves());
		assertEquals("invalidates for actual " + actual, expected.invalidates(), actual.invalidates());
	}

	private <I, R> void assertStop(EventEngine<I, R> engine) {
		assertFalse("engine has no next", engine.hasNext());
	}
}
