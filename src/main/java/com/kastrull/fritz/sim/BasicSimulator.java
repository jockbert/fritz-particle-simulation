package com.kastrull.fritz.sim;

import static com.codepoetics.protonpack.StreamUtils.stream;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Stream;

import com.codepoetics.protonpack.StreamUtils;
import com.kastrull.fritz.physics.Physics;
import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.sim.event.Event;
import com.kastrull.fritz.sim.event.SimEndEvent;

public class BasicSimulator implements Simulator {

	private final Physics physics;

	class SimError extends RuntimeException {
		/** */
		private static final long serialVersionUID = 1L;

		SimError(String message) {
			super(message);
		}
	}

	final class Context {
		public final Physics phy;
		public final Register register;
		public final List<Border> walls;
		public double atTime;

		Context(Physics phy, Register register, List<Border> walls, double atTime) {
			this.phy = phy;
			this.register = register;
			this.walls = walls;
			this.atTime = atTime;
		}
	}

	final class WallHit extends Event {

		public final double atTime;
		public final Border wall;
		public final Integer pid;
		private final Context ctx;

		public

		WallHit(double atTime, Border wall, Integer pid, Context ctx) {
			super(atTime);
			this.atTime = atTime;
			this.wall = wall;
			this.pid = pid;
			this.ctx = ctx;
		}

		@Override
		public boolean apply() {
			// XXX need fixing: wall momentum
			ctx.register.modify(pid, atTime,
				particle -> ctx.phy.interactWall(particle, wall).p);

			return false;
		}

		@Override
		public Stream<Event> next() {
			return findWallCollision(ctx).apply(pid);
		}
	}

	BasicSimulator(Physics phy) {
		this.physics = phy;

	}

	@Override
	public SimState simulate(final SimState startState) {
		Register register = new Register(startState.particles(), startState.currentTime());

		PriorityQueue<Event> eventQueue = new PriorityQueue<>();

		Context context = new Context(
			physics,
			register,
			startState.walls(),
			startState.currentTime());

		eventQueue.add(new SimEndEvent(startState.targetTime()));

		register
			.ids()
			.flatMap(findWallCollision(context))
			.forEach(eventQueue::add);

		while (!eventQueue.isEmpty()) {
			Event event = eventQueue.poll();
			context.atTime = event.atTime();
			if (event.apply()) {
				double targetTime = startState.targetTime();
				return startState
					.currentTime(targetTime)
					.particles(register.toList(targetTime));
			}

			event.next().forEach(eventQueue::add);
		}

		throw new SimError("EventQueue is some how empty. Lost an expected simulation end event");
	}

	private Function<Integer, Stream<Event>> findWallCollision(Context ctx) {

		return pid -> {

			Particle particle = ctx.register.getAtTime(pid, ctx.atTime);

			Function<Border, Optional<Event>> toWallHit = wall -> ctx.phy
				.collisionTimeWall(particle, wall)
				.map(atTime -> new WallHit(atTime + ctx.atTime, wall, pid, ctx));

			// XXX Include in event comparator
			// Comparator<Event> minTimeComparator = (w1, w2) -> Double
			// .compare(w1.atTime, w2.atTime);

			Optional<Event> maybeHit = ctx.walls.stream()
				.map(toWallHit)
				.flatMap(StreamUtils::stream).min(Event::compareTo);

			return stream(maybeHit);
		};
	}
}
