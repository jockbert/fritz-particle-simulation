package com.kastrull.fritz.sim;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	private EventEngine<SimParticle, EventAndAction> engine = new BasicEventEngine<>();

	private List<SimParticle> particles;

	private SimState initialState;

	private double totalMomentum = 0;

	public EngineDrivenSimulator(LinearPhysics linearPhysics) {
		this.physics = linearPhysics;
	}

	@Override
	public SimState simulate(SimState state) {
		initialState = state;

		initParticles(state);

		initWalls(state);

		addSimulationEnd(state);

		addAllWallHits(state);

		addAllParticleHits(state);

		Optional<Double> endTime = Optional.empty();

		simloop: for (Outcome<SimParticle, EventAndAction> oc : engine) {

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
					.forEach(p -> {
						addParticleHit(p, oc.time());
						addWallHit(p, oc.time());
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

	private void initParticles(SimState state) {
		particles = state.particles().stream()
			.map(p -> new SimParticle(p, state.currentTime()))
			.collect(Collectors.toList());
	}

	private void addAllParticleHits(SimState state) {
		particles.forEach(p -> addParticleHit(p, state.currentTime()));
	}

	private void addParticleHit(SimParticle p, double currentTime) {
		Particle particle = p.getAtTime(currentTime);

		particles
			.stream()
			.filter(otherP -> otherP != p)
			.forEach(otherP -> physics
				.collisionTime(particle, otherP.getAtTime(currentTime))
				.ifPresent(hitTime -> engine
					.addEvent(
						currentTime + hitTime,
						ena(
							Event.PARTICLE_HIT,
							particleHitAction(p, otherP, currentTime + hitTime)),
						p, otherP)));
	}

	private EventAndAction ena(Event e, Runnable a) {
		return new EventAndAction(e, a);
	}

	private Runnable particleHitAction(
			SimParticle p1,
			SimParticle p2,
			double particleHitTime) {

		return () -> {
			Interaction i = physics.interact(
				p1.getAtTime(particleHitTime),
				p2.getAtTime(particleHitTime));

			p1.modify(i.p1, particleHitTime);
			p2.modify(i.p2, particleHitTime);
		};
	}

	private void addAllWallHits(SimState state) {
		particles.stream().forEach(p -> addWallHit(p, state.currentTime()));
	}

	private void addWallHit(SimParticle p, double currentTime) {
		Particle particle = p.getAtTime(currentTime);

		initialState
			.walls()
			.stream()
			.forEach(wall -> physics
				.collisionTimeWall(particle, wall)
				.ifPresent(hitTime -> engine
					.addEvent(
						currentTime + hitTime,
						ena(Event.WALL_HIT,
							wallHitAction(p, wall, currentTime + hitTime)),
						p)));
	}

	private Runnable wallHitAction(
			SimParticle p,
			Border wall,
			double wallHitTime) {

		return () -> {
			WallInteraction wi = physics.interactWall(p.getAtTime(wallHitTime), wall);
			totalMomentum += wi.wallMomentum.distance();
			p.modify(wi.p, wallHitTime);
		};
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
			.particles(particles.stream()
				.map(p -> p.getAtTime(state.targetTime()))
				.collect(Collectors.toList()));
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
