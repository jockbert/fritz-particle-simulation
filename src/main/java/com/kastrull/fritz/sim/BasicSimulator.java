package com.kastrull.fritz.sim;

import java.util.PriorityQueue;

import com.kastrull.fritz.sim.event.Event;
import com.kastrull.fritz.sim.event.SimEndEvent;

public class BasicSimulator implements Simulator {

	class SimError extends RuntimeException {
		/** */
		private static final long serialVersionUID = 1L;

		SimError(String message) {
			super(message);
		}
	}

	@Override
	public SimState simulate(final SimState startState) {

		PriorityQueue<Event> eventQueue = new PriorityQueue<>();
		eventQueue.add(new SimEndEvent(startState.targetTime()));

		while (!eventQueue.isEmpty()) {
			Event event = eventQueue.poll();
			if (event.apply()) {
				return startState.currentTime(startState.targetTime());
			}
		}

		throw new SimError("EventQueue is some how empty. Lost an expected simulation end event");
	}
}
