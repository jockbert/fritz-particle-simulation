package com.kastrull.fritz.sim;

import com.kastrull.fritz.engine.Action;
import com.kastrull.fritz.engine.BasicEventEngine;
import com.kastrull.fritz.engine.EventEngine;
import com.kastrull.fritz.engine.Outcome;
import com.kastrull.fritz.physics.LinearPhysics;
import com.kastrull.fritz.physics.Physics;
import com.kastrull.fritz.primitives.Border;
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

		for (Outcome<Event> oc : engine) {

			Event event = oc.result();

			switch (event) {

			case SIM_END:
				return endState(state);

			case WALL_HIT:

				oc
					.involves()
					.forEach(
						pid ->
						// TODO Add both wall hit and particle collision
						addWallHit(pid, oc.time()));

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
	WALL_HIT, SIM_END
}
