package com.kastrull.fritz.sim.event;

import java.util.stream.Stream;

import com.kastrull.fritz.primitives.Interaction;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.sim.Context;

public class ParticleHit extends Event {

	private Integer pidA;
	private Integer pidB;
	private Context ctx;

	public ParticleHit(double atTime, Integer pidA, Integer pidB, Context ctx) {
		super(atTime);
		this.pidA = pidA;
		this.pidB = pidB;
		this.ctx = ctx;
	}

	@Override
	public boolean apply() {

		Particle pA = ctx.register.getAtTime(pidA, atTime());
		Particle pB = ctx.register.getAtTime(pidB, atTime());

		ctx.phy.collisionTime(pA, pB).ifPresent(time -> {
			// TODO BUG:
			// Only do interaction if already not altered
			// Avoid phantom changes caused by double
			// interactions in queue.

			Interaction interaction = ctx.phy.interact(pA, pB);

			ctx.register.modify(pidA, atTime(),
				ignored -> interaction.p1);

			ctx.register.modify(pidB, atTime(),
				ignored -> interaction.p2);
		});
		return false;
	}

	@Override
	public Stream<Integer> getPids() {
		return Stream.of(pidA, pidB);
	}
}
