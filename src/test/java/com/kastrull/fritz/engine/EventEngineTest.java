package com.kastrull.fritz.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EventEngineTest {

	@Test
	public void testCircularEventsUsingTomatoDecay() {

		EventEngine<Integer> engine = EventEngine.create();

		final Action<Integer> tomatoDecay = new Action<Integer>() {
			int freshness = 2;

			@Override
			public Integer apply(Double time) {
				if (freshness > 0)
					engine.addEvent(time + 0.2, this);
				return freshness--;
			}
		};

		engine.addEvent(1.0, tomatoDecay);

		assertNext(engine, 1.0, 2);
		assertNext(engine, 1.2, 1);
		assertNext(engine, 1.4, 0);
		assertStop(engine);
	}

	@Test
	public void testInterceptingEventsUsingHareAndTurtle() {

		EventEngine<String> engine = EventEngine.create();

		int hare = 1;
		int turtle = 2;
		int winner = 3;

		engine.addEvent(
			12.3,
			time -> "turtle wins race",
			turtle, winner);

		engine.addEvent(
			7.8,
			time -> "hare intercepts turtle and wins race",
			hare, winner);

		assertNext(
			engine,
			Outcome.of(
				7.8,
				"hare intercepts turtle and wins race")
				.involves(hare, winner)
				.invalidates(turtle));

		assertStop(engine);
	}

	@Test
	public void testUnrelatedEvents() {

		EventEngine<Boolean> engine = EventEngine.create();

		int eatApplePie = 0;
		int theEarthRevolves = 1;
		int throwADice = 2;

		engine.addEvent(1.1, __ -> true, eatApplePie);
		engine.addEvent(3.3, __ -> true, throwADice);
		engine.addEvent(2.2, __ -> false, theEarthRevolves);

		assertNext(engine,
			Outcome.of(1.1, true).involves(eatApplePie));
		assertNext(engine,
			Outcome.of(2.2, false).involves(theEarthRevolves));
		assertNext(engine,
			Outcome.of(3.3, true).involves(throwADice));
	}

	private <R> void assertNext(EventEngine<R> engine, Outcome<R> expected) {
		assertTrue("engine has next", engine.hasNext());
		Outcome<R> actual = engine.next();
		assertEquals("time", expected.time(), actual.time(), Math.ulp(expected.time()));
		assertEquals("result", expected.result(), actual.result());
		assertEquals("involves", expected.involves(), actual.involves());
		assertEquals("invalidates", expected.invalidates(), actual.invalidates());
	}

	private <R> void assertNext(EventEngine<R> engine, double time, R result) {
		assertNext(engine, Outcome.of(time, result));
	}

	private <R> void assertStop(EventEngine<R> engine) {
		assertFalse("engine has no next", engine.hasNext());
	}
}
