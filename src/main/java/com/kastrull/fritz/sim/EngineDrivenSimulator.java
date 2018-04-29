package com.kastrull.fritz.sim;

import com.kastrull.fritz.engine.Action;
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

	private static Physics PHY = new LinearPhysics();

	private EventEngine<Event> engine = new BasicEventEngine<>();

	private Register particles;

	private SimState initialState;

	private double totalMomentum = 0;

	@Override
	public SimState simulate(SimState state) {
		initialState = state;

		initRegister(state);
		initWalls(state);

		addSimulationEnd(state);

		addAllWallHits(state);

		addAllParticleHits(state);

		for (Outcome<Event> oc : engine) {

			Event event = oc.result();

			switch (event) {

			case SIM_END:
				return endState(state);

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

		return null;
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

				PHY
					.collisionTime(particle, otherParticle)
					.ifPresent(hitTime -> engine
						.addEvent(
							currentTime + hitTime,
							particleHitAction(particleId, otherParticleId),
							particleId, otherParticleId));
			});
	}

	private Action<Event> particleHitAction(Integer pId1, Integer pId2) {
		return particleHitTime -> {
			Particle p1Before = idToParticle(pId1, particleHitTime);
			Particle p2Before = idToParticle(pId2, particleHitTime);

			Interaction i = PHY.interact(p1Before, p2Before);

			// update changed particle after wall collisions
			particles.particles.set(pId1, i.p1);
			particles.particles.set(pId2, i.p2);

			particles.times[pId1] = particleHitTime;
			particles.times[pId2] = particleHitTime;

			return Event.PARTICLE_HIT;
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
			.forEach(wall -> PHY
				.collisionTimeWall(particle, wall)
				.ifPresent(hitTime -> engine
					.addEvent(
						currentTime + hitTime,
						wallHitAction(particleId, wall),
						particleId)));
	}

	private Action<Event> wallHitAction(final Integer particleId, final Border wall) {
		return wallHitTime -> {
			Particle particleBefore = idToParticle(particleId, wallHitTime);
			WallInteraction wi = PHY.interactWall(particleBefore, wall);

			totalMomentum += wi.wallMomentum.distance();

			// update changed particle after wall collisions
			particles.particles.set(particleId, wi.p);
			particles.times[particleId] = wallHitTime;

			return Event.WALL_HIT;
		};
	}

	private Particle idToParticle(Integer particleId, double currentTime) {
		return particles.getAtTime(particleId, currentTime);
	}

	private void addSimulationEnd(SimState state) {
		engine.addEvent(state.targetTime(), Event.SIM_END);
	}

	private SimState endState(SimState state) {
		return state
			.currentTime(state.targetTime())
			.wallAbsorbedMomentum(totalMomentum)
			.particles(particles.toList(state.targetTime()));
	}
}

enum Event {
	WALL_HIT, SIM_END, PARTICLE_HIT
}
