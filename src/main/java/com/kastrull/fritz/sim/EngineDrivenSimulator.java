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
	private EventEngine<SimParticle, Event> engine = new BasicEventEngine<>();
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

		simloop: for (Outcome<SimParticle, Event> oc : engine) {

			boolean isAfterSimEndTime = endTime.isPresent() && endTime.get() < oc.time();
			if (isAfterSimEndTime)
				break simloop;

			Event event = oc.result();
			totalMomentum += event.interact(oc.time());
			endTime = event.isAtEnd() ? Optional.of(oc.time()) : endTime;
		}

		return endState(state);
	}

	private void addParticleEventsToEngine(SimParticle p, double currentTime) {
		addParticleHit(p, currentTime);
		addWallHit(p, currentTime);
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
				.ifPresent(hitTime -> engine.addEvent(
					currentTime + hitTime,
					new ParticleHit(p, otherP),
					p, otherP)));
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
				.ifPresent(hitTime -> engine.addEvent(
					currentTime + hitTime,
					new WallHit(p, wall),
					p)));
	}

	private void addSimulationEnd(SimState state) {
		engine.addEvent(state.targetTime(), new SimEnd());
	}

	private SimState endState(SimState state) {
		return SimState.mut(state)
			.currentTime(state.targetTime())
			.wallAbsorbedMomentum(totalMomentum)
			.particles(particles.stream()
				.map(p -> p.getAtTime(state.targetTime()))
				.collect(Collectors.toList()));
	}

	abstract class Event {

		/** Modify particles and return wall momentum. */
		double interact(double time) {
			// do nothing
			return 0;
		}

		boolean isAtEnd() {
			// do not end
			return false;
		}
	}

	final class SimEnd extends Event {
		@Override
		boolean isAtEnd() {
			return true;
		}
	}

	final class WallHit extends Event {
		private SimParticle p;
		private Border border;

		WallHit(SimParticle p, Border border) {
			this.p = p;
			this.border = border;
		}

		@Override
		double interact(double time) {
			WallInteraction wi = physics.interactWall(p.getAtTime(time), border);
			p.modify(wi.p, time);

			addParticleEventsToEngine(p, time);
			return wi.wallMomentum.distance();
		}
	}

	final class ParticleHit extends Event {
		private SimParticle p1, p2;

		ParticleHit(SimParticle p1, SimParticle p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		@Override
		double interact(double time) {
			Interaction i = physics.interact(
				p1.getAtTime(time),
				p2.getAtTime(time));

			p1.modify(i.p1, time);
			p2.modify(i.p2, time);

			addParticleEventsToEngine(p1, time);
			addParticleEventsToEngine(p2, time);
			return 0;
		}
	}
}
