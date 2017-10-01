package com.kastrull.fritz.sim;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Stream;

import com.kastrull.fritz.physics.Physics;
import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.sim.event.Event;
import com.kastrull.fritz.sim.event.SimEndEvent;

public class BasicSimulator implements Simulator {

	private final Physics phy;

	class SimError extends RuntimeException {
		/** */
		private static final long serialVersionUID = 1L;

		SimError(String message) {
			super(message);
		}
	}

	final class WallHit extends Event {

		public final double atTime;
		public final Border wall;
		public final Integer pid;
		private final Register reg;

		public

		WallHit(double atTime, Border wall, Integer pid, Register reg) {
			super(atTime);
			this.atTime = atTime;
			this.wall = wall;
			this.pid = pid;
			this.reg = reg;
		}

		@Override
		public boolean apply() {
			// XXX need fixing: wall momentum
			reg.modify(pid, atTime,
				particle -> phy.interactWall(particle, wall).p);
			return false;
		}
	}

	BasicSimulator(Physics phy) {
		this.phy = phy;

	}

	@Override
	public SimState simulate(final SimState startState) {
		Register register = new Register(startState.particles(), startState.currentTime());

		PriorityQueue<Event> eventQueue = new PriorityQueue<>();
		eventQueue.add(new SimEndEvent(startState.targetTime()));

		startState.particles();

		Stream<WallHit> nextWallCollision = register.ids()
			.flatMap(findWallCollision(register, startState.walls()));

		nextWallCollision.forEach(eventQueue::add);

		while (!eventQueue.isEmpty()) {
			Event event = eventQueue.poll();

			if (event.apply()) {
				double targetTime = startState.targetTime();
				return startState
					.currentTime(targetTime)
					.particles(register.toList(targetTime));
			}

		}

		throw new SimError("EventQueue is some how empty. Lost an expected simulation end event");
	}

	private Function<Integer, Stream<WallHit>> findWallCollision(Register register, List<Border> walls) {

		return pid -> {

			// XXX Get at initial time. Make it explicit instead of implicit.
			Particle particle = register.get(pid);

			Function<Border, Optional<WallHit>> toWallHit = wall -> phy
				.collisionTime(particle, wall)
				.map(atTime -> new WallHit(atTime, wall, pid, register));

			Comparator<WallHit> minTimeComparator = (w1, w2) -> Double
				.compare(w1.atTime, w2.atTime);

			Optional<WallHit> maybeHit = walls.stream()
				.map(toWallHit)
				.flatMap(this::optToStream)
				.min(minTimeComparator);

			return optToStream(maybeHit);
		};
	}

	private <T> Stream<T> optToStream(Optional<T> t) {
		return t.isPresent() ? Stream.of(t.get()) : Stream.empty();
	}
}
