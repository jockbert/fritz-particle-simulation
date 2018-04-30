package com.kastrull.fritz.sim;

import java.util.Optional;

import com.kastrull.fritz.engine.BasicEventEngine;
import com.kastrull.fritz.engine.EventEngine;
import com.kastrull.fritz.engine.Outcome;
import com.kastrull.fritz.physics.LinearPhysics;
import com.kastrull.fritz.physics.Physics;
import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Interaction;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.primitives.WallInteraction;

public class EngineDrivenSimulator implements Simulator {

	private Physics physics;

	private EventEngine<EventAndAction> engine = new BasicEventEngine<>();

	private Register particles;

	private SimState initialState;

	private double totalMomentum = 0;

	public EngineDrivenSimulator(LinearPhysics linearPhysics) {
		this.physics = linearPhysics;
	}

	@Override
	public SimState simulate(SimState state) {
		initialState = state;

		initRegister(state);
		initWalls(state);

		addSimulationEnd(state);

		addAllWallHits(state);

		addAllParticleHits(state);

		Optional<Double> endTime = Optional.empty();

		simloop: for (Outcome<EventAndAction> oc : engine) {

			boolean isAfterSimEndTime = endTime.isPresent() && endTime.get() < oc.time();
			if (isAfterSimEndTime)
				break simloop;

			// Result has side effects, so must be executed after
			// breaking simulation loop.
			EventAndAction ena = oc.result();
			Event event = ena.event;
			ena.action.run();

			switch (event) {

			case SIM_END:
				// Continue simulation to consume all
				// events happening at the same time.
				endTime = Optional.of(Double.valueOf(oc.time()));
				break;

			case WALL_HIT:
			case PARTICLE_HIT:

				oc
					.involves()
					.forEach(pid -> {
						addParticleHit(pid, oc.time());
						addWallHit(pid, oc.time());
					});

				break;

			default:
				throw new RuntimeException("Can't handle event type " + event);
			}
		}

		return endState(state);
	}

	private void initWalls(SimState state) {
		state.walls();
	}

	private void initRegister(SimState state) {
		particles = new Register(state.particles(), state.currentTime());
	}

	private void addAllParticleHits(SimState state) {
		particles.ids().forEach(pId -> addParticleHit(pId, state.currentTime()));
	}

	private void addParticleHit(Integer particleId, double currentTime) {
		Particle particle = idToParticle(particleId, currentTime);

		particles
			.ids()
			.filter(otherId -> otherId != particleId)
			.forEach(otherParticleId -> {
				Particle otherParticle = particles.getAtTime(otherParticleId, currentTime);

				physics
					.collisionTime(particle, otherParticle)
					.ifPresent(hitTime -> engine
						.addEvent(
							currentTime + hitTime,
							ena(
								Event.PARTICLE_HIT,
								particleHitAction(particleId, otherParticleId, currentTime + hitTime)),
							particleId, otherParticleId));
			});
	}

	private EventAndAction ena(Event e, Runnable a) {
		return new EventAndAction(e, a);
	}

	private Runnable particleHitAction(
			Integer pId1,
			Integer pId2,
			double particleHitTime) {

		return () -> {
			Particle p1Before = idToParticle(pId1, particleHitTime);
			Particle p2Before = idToParticle(pId2, particleHitTime);

			Interaction i = physics.interact(p1Before, p2Before);

			// update changed particle after wall collisions
			particles.particles.set(pId1, i.p1);
			particles.particles.set(pId2, i.p2);

			particles.times[pId1] = particleHitTime;
			particles.times[pId2] = particleHitTime;
		};
	}

	private void addAllWallHits(SimState state) {
		particles.ids().forEach(pId -> addWallHit(pId, state.currentTime()));
	}

	private void addWallHit(Integer particleId, double currentTime) {
		Particle particle = idToParticle(particleId, currentTime);

		initialState
			.walls()
			.stream()
			.forEach(wall -> physics
				.collisionTimeWall(particle, wall)
				.ifPresent(hitTime -> engine
					.addEvent(
						currentTime + hitTime,
						ena(Event.WALL_HIT,
							wallHitAction(particleId, wall, currentTime + hitTime)),
						particleId)));
	}

	private Runnable wallHitAction(
			final Integer particleId,
			final Border wall,
			final double wallHitTime) {

		return () -> {
			Particle particleBefore = idToParticle(particleId, wallHitTime);
			WallInteraction wi = physics.interactWall(particleBefore, wall);

			totalMomentum += wi.wallMomentum.distance();

			// update changed particle after wall collisions
			particles.particles.set(particleId, wi.p);
			particles.times[particleId] = wallHitTime;
		};
	}

	private Particle idToParticle(Integer particleId, double currentTime) {
		return particles.getAtTime(particleId, currentTime);
	}

	private void addSimulationEnd(SimState state) {
		engine.addEvent(
			state.targetTime(),
			ena(
				Event.SIM_END,
				() -> {
					/* do nothing */ }));
	}

	private SimState endState(SimState state) {
		return SimState.mut(state)
			.currentTime(state.targetTime())
			.wallAbsorbedMomentum(totalMomentum)
			.particles(particles.toList(state.targetTime()));
	}
}

enum Event {
	WALL_HIT, SIM_END, PARTICLE_HIT
}

class EventAndAction {
	public final Event event;
	public final Runnable action;

	EventAndAction(Event e, Runnable a) {
		this.event = e;
		this.action = a;
	}
}
